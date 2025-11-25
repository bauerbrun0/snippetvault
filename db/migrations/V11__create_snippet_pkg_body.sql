-------------------------------------------------------------------------
-- V11__create_snippet_pkg_body.sql                                    --
-------------------------------------------------------------------------
-- Creates the snippet package's body                                  --
-------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE BODY snippet_pkg AS
    PROCEDURE create_snippet(
        p_user_id IN NUMBER,
        p_title IN VARCHAR2,
        p_description IN VARCHAR2,
        p_snippet OUT SYS_REFCURSOR
    ) AS
        v_id          NUMBER;
        v_user_id     NUMBER;
        v_title       VARCHAR2(255 CHAR);
        v_description VARCHAR2(4000 CHAR);
        v_created     TIMESTAMP;
        v_updated     TIMESTAMP;
    BEGIN
        INSERT INTO snippet (user_id, title, description)
        VALUES (p_user_id, p_title, p_description)
        RETURN id,
            user_id,
            title,
            description,
            created,
            updated
        INTO v_id,
            v_user_id,
            v_title,
            v_description,
            v_created,
            v_updated;

        OPEN p_snippet FOR
            SELECT v_id          AS id,
                   v_user_id     AS user_id,
                   v_title       AS title,
                   v_description AS description,
                   v_created     AS created,
                   v_updated     AS updated
            FROM dual;
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_SNIPPET_USER%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
                ELSE
                    RAISE;
                END IF;
            ELSE
                RAISE;
            END IF;
    END create_snippet;
    PROCEDURE get_paginated_snippets(
        p_user_id IN NUMBER,
        p_search_query IN VARCHAR2,
        p_tag_ids IN number_array,
        p_language_ids IN number_array,
        p_page_number IN NUMBER,
        p_page_size IN NUMBER,
        p_total_count OUT NUMBER,
        p_snippets OUT SYS_REFCURSOR
    ) AS
        v_search_query VARCHAR2(4000);
        v_page_number NUMBER;
        v_page_size   NUMBER;
    BEGIN
        v_search_query := TRIM(p_search_query);
        v_page_number := NVL(p_page_number, 1);
        v_page_size := NVL(p_page_size, 20);

        -- Get the total count
        SELECT total_count
        INTO p_total_count
        FROM (
            WITH base_filtered AS (
                SELECT v.id,
                        CASE
                            WHEN v_search_query IS NULL THEN 1
                            WHEN LOWER(v.title) LIKE '%' || LOWER(v_search_query) || '%' THEN 1
                            WHEN LOWER(v.description) LIKE '%' || LOWER(v_search_query) || '%' THEN 2
                            WHEN LOWER(v.filename) LIKE '%' || LOWER(v_search_query) || '%' THEN 3
                            WHEN CONTAINS(v.content, v_search_query) > 0 THEN 4
                            ELSE 5
                        END AS relevance
                        FROM vw_snippet_search v
                        WHERE v.user_id = p_user_id
                        AND
                            (
                                p_tag_ids IS NULL
                                OR CARDINALITY(p_tag_ids) = 0
                                OR v.tag_id MEMBER OF p_tag_ids
                            )
                        AND
                            (
                                p_language_ids IS NULL
                                OR CARDINALITY(p_language_ids) = 0
                                OR v.language_id MEMBER OF p_language_ids
                            )
                ), filtered AS (
                    SELECT id, relevance
                    FROM base_filtered
                    WHERE v_search_query IS NULL OR relevance < 5
                ), deduplicated AS (
                    SELECT id,
                            MIN(relevance) AS relevance
                    FROM filtered
                    GROUP BY id
                )
                SELECT count(*) as total_count
                FROM deduplicated
            );

        -- get the result
        OPEN p_snippets FOR
        WITH base_filtered AS (
            SELECT v.id,
                    CASE
                        WHEN v_search_query IS NULL THEN 1
                        WHEN LOWER(v.title) LIKE '%' || LOWER(v_search_query) || '%' THEN 1
                        WHEN LOWER(v.description) LIKE '%' || LOWER(v_search_query) || '%' THEN 2
                        WHEN LOWER(v.filename) LIKE '%' || LOWER(v_search_query) || '%' THEN 3
                        WHEN LOWER(v.content) LIKE '%' || LOWER(v_search_query) || '%' THEN 4
                        ELSE 5
                    END AS relevance
                    FROM vw_snippet_search v
                    WHERE v.user_id = p_user_id
                    AND
                        (
                            p_tag_ids IS NULL
                            OR CARDINALITY(p_tag_ids) = 0
                            OR v.tag_id MEMBER OF p_tag_ids
                        )
                    AND
                        (
                            p_language_ids IS NULL
                            OR CARDINALITY(p_language_ids) = 0
                            OR v.language_id MEMBER OF p_language_ids
                        )
            ), filtered AS (
                SELECT bf.id, bf.relevance
                FROM base_filtered bf
                WHERE v_search_query IS NULL OR relevance < 5
            ), deduplicated AS (
                SELECT f.id,
                        MIN(f.relevance) AS relevance
                FROM filtered f
                GROUP BY f.id
            ), extended AS (
                SELECT d.id,
                       d.relevance,
                       s.user_id,
                       s.title,
                       s.description,
                       s.created,
                       s.updated,
                       count(DISTINCT v.file_id) AS file_count,
                       cast(collect( v.language_id ) AS number_array) AS language_ids,
                       cast(collect( v.tag_id ) AS number_array) AS tag_ids,
                       ROW_NUMBER() OVER (
                            ORDER BY d.relevance, s.created DESC, d.id
                        ) AS row_number
                FROM deduplicated d
                JOIN snippet s ON s.id = d.id
                LEFT JOIN vw_snippet_search v ON v.id = d.id
                GROUP BY
                    d.id, d.relevance,
                    s.user_id, s.title, s.description, s.created, s.updated
            )
            SELECT id,
                   user_id,
                   title,
                   description,
                   created,
                   updated,
                   relevance,
                   file_count,
                   language_ids,
                   tag_ids
            FROM extended e
            WHERE row_number BETWEEN
                (v_page_number - 1) * v_page_size + 1 AND
                v_page_number * v_page_size
            ORDER BY row_number;
    END get_paginated_snippets;

    FUNCTION get_snippet(p_id IN NUMBER) RETURN  SYS_REFCURSOR AS
        v_count NUMBER;
        v_snippet SYS_REFCURSOR;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM snippet
        WHERE id = p_id;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_SNIPPET_NOT_FOUND, 'Snippet not found');
        END IF;

        OPEN v_snippet FOR
            SELECT id,
                   user_id,
                   title,
                   description,
                   created,
                   updated
            FROM snippet
            WHERE id = p_id;
        RETURN v_snippet;
    END get_snippet;

    PROCEDURE delete_snippet(p_id IN NUMBER, p_snippet OUT SYS_REFCURSOR) AS
        v_id NUMBER;
        v_user_id NUMBER;
        v_title VARCHAR2(255 CHAR);
        v_description VARCHAR2(4000 CHAR);
        v_created TIMESTAMP;
        v_updated TIMESTAMP;
    BEGIN
        DELETE FROM snippet WHERE id = p_id
        RETURNING id,
            user_id,
            title,
            description,
            created,
            updated
        INTO v_id,
            v_user_id,
            v_title,
            v_description,
            v_created,
            v_updated;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_SNIPPET_NOT_FOUND, 'Snippet not found');
        END IF;

        OPEN p_snippet FOR
            SELECT v_id AS id,
                   v_user_id AS user_id,
                   v_title AS title,
                   v_description AS description,
                   v_created AS created,
                   v_updated AS updated
            FROM dual;
    END delete_snippet;

    PROCEDURE update_snippet(
        p_id IN NUMBER,
        p_title IN VARCHAR2,
        p_description IN VARCHAR2,
        p_snippet OUT SYS_REFCURSOR
    ) AS
        v_id NUMBER;
        v_user_id NUMBER;
        v_title VARCHAR2(255 CHAR);
        v_description VARCHAR2(4000 CHAR);
        v_created TIMESTAMP;
        v_updated TIMESTAMP;
    BEGIN
        UPDATE snippet
        SET title = COALESCE(p_title, title),
            description = COALESCE(p_description, description)
        WHERE id = p_id
        RETURNING id,
            user_id,
            title,
            description,
            created,
            updated
        INTO v_id,
            v_user_id,
            v_title,
            v_description,
            v_created,
            v_updated;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_SNIPPET_NOT_FOUND, 'Snippet not found');
        END IF;

        OPEN p_snippet FOR
            SELECT v_id AS id,
                   v_user_id AS user_id,
                   v_title AS title,
                   v_description AS description,
                   v_created AS created,
                   v_updated AS updated
            FROM dual;
    END update_snippet;

    PROCEDURE add_tag_to_snippet(p_snippet_id IN NUMBER, p_tag_id IN NUMBER) AS
    BEGIN
        INSERT INTO snippet_tag (tag_id, snippet_id)
        VALUES (p_tag_id, p_snippet_id);

    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            IF SQLERRM LIKE '%UQ_SNIPPET_TAG%' THEN
                RAISE_APPLICATION_ERROR(constants_pkg.ERR_DUPLICATE_TAG_ON_SNIPPET, 'Tag already exists on snippet');
            ELSE
                RAISE;
            END IF;
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_SNIPPET_TAG_SNIPPET%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_SNIPPET_NOT_FOUND, 'Snippet not found');
                ELSIF SQLERRM LIKE '%FK_SNIPPET_TAG_TAG%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_NOT_FOUND, 'Tag not found');
                ELSE
                    RAISE;
                END IF;
            ELSE
                RAISE;
            END IF;
    END add_tag_to_snippet;

    PROCEDURE remove_tag_from_snippet(p_snippet_id IN NUMBER, p_tag_id IN NUMBER) AS
    BEGIN
        DELETE
        FROM snippet_tag
        WHERE snippet_id = p_snippet_id
          AND tag_id = p_tag_id;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_NOT_ON_SNIPPET, 'Tag is not on snippet');
        END IF;
    END;

    PROCEDURE create_file(
        p_snippet_id IN NUMBER,
        p_filename IN VARCHAR2,
        p_content IN CLOB,
        p_language_id IN NUMBER,
        p_file OUT SYS_REFCURSOR
    ) AS
        v_id NUMBER;
        v_snippet_id NUMBER;
        v_title VARCHAR2(255 CHAR);
        v_content CLOB;
        v_language_id NUMBER;
        v_created TIMESTAMP;
        v_updated TIMESTAMP;
    BEGIN
        INSERT INTO snippetvault_file (snippet_id, filename, content, language_id)
        VALUES (
                   p_snippet_id, p_filename, p_content, p_language_id
               ) RETURNING id,
            snippet_id,
            filename,
            content,
            language_id,
            created,
            updated
        INTO v_id,
            v_snippet_id,
            v_title,
            v_content,
            v_language_id,
            v_created,
            v_updated;

        OPEN p_file FOR
            SELECT v_id          AS id,
                   v_snippet_id  AS snippet_id,
                   v_title       AS filename,
                   v_content     AS content,
                   v_language_id AS language_id,
                   v_created     AS created,
                   v_updated     AS updated
            FROM dual;

    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_FILE_SNIPPET%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_SNIPPET_NOT_FOUND, 'Snippet not found');
                ELSIF SQLERRM LIKE '%FK_FILE_LANGUAGE%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_LANGUAGE_NOT_FOUND, 'Language not found');
                ELSE
                    RAISE;
                END IF;
            ELSE
                RAISE;
            end if;
    END create_file;

    FUNCTION get_files_of_snippet(p_snippet_id IN NUMBER) RETURN SYS_REFCURSOR AS
        v_files SYS_REFCURSOR;
    BEGIN
        OPEN v_files FOR
            SELECT id,
                   snippet_id,
                   filename,
                   content,
                   language_id,
                   created,
                   updated
            FROM snippetvault_file
            WHERE snippet_id = p_snippet_id;
        RETURN v_files;
    END get_files_of_snippet;

    FUNCTION get_file(p_id IN NUMBER) RETURN SYS_REFCURSOR AS
        v_file SYS_REFCURSOR;
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM snippetvault_file
        WHERE id = p_id;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_FILE_NOT_FOUND, 'File not found');
        END IF;

        OPEN v_file FOR
            SELECT id,
                   snippet_id,
                   filename,
                   content,
                   language_id,
                   created,
                   updated
            FROM snippetvault_file
            WHERE id = p_id;
        RETURN v_file;
    END get_file;

    PROCEDURE update_file(
        p_id IN NUMBER,
        p_filename IN VARCHAR2 DEFAULT NULL,
        p_content IN CLOB DEFAULT NULL,
        p_language_id IN NUMBER DEFAULT NULL,
        p_file OUT SYS_REFCURSOR
    ) AS
        v_id NUMBER;
        v_snippet_id NUMBER;
        v_title VARCHAR2(255 CHAR);
        v_content CLOB;
        v_language_id NUMBER;
        v_created TIMESTAMP;
        v_updated TIMESTAMP;
    BEGIN
        UPDATE snippetvault_file
        SET filename = COALESCE(p_filename, filename),
            content = COALESCE(p_content, content),
            language_id = COALESCE(p_language_id, language_id)
        WHERE id = p_id
        RETURNING id,
            snippet_id,
            filename,
            content,
            language_id,
            created,
            updated
        INTO v_id,
            v_snippet_id,
            v_title,
            v_content,
            v_language_id,
            v_created,
            v_updated;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_FILE_NOT_FOUND, 'File not found');
        END IF;

        OPEN p_file FOR
            SELECT v_id          AS id,
                   v_snippet_id  AS snippet_id,
                   v_title       AS filename,
                   v_content     AS content,
                   v_language_id AS language_id,
                   v_created     AS created,
                   v_updated     AS updated
            FROM dual;

        EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_FILE_LANGUAGE%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_LANGUAGE_NOT_FOUND, 'Language not found');
                ELSE
                    RAISE;
                END IF;
            ELSE
                RAISE;
            END IF;
    END update_file;

    PROCEDURE delete_file(p_id IN NUMBER, p_file OUT SYS_REFCURSOR) AS
        v_id NUMBER;
        v_snippet_id NUMBER;
        v_title VARCHAR2(255 CHAR);
        v_content CLOB;
        v_language_id NUMBER;
        v_created TIMESTAMP;
        v_updated TIMESTAMP;
    BEGIN
        DELETE FROM snippetvault_file WHERE id = p_id
        RETURNING id,
            snippet_id,
            filename,
            content,
            language_id,
            created,
            updated
        INTO v_id,
            v_snippet_id,
            v_title,
            v_content,
            v_language_id,
            v_created,
            v_updated;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_FILE_NOT_FOUND, 'File not found');
        END IF;

        OPEN p_file FOR
            SELECT v_id          AS id,
                   v_snippet_id  AS snippet_id,
                   v_title       AS filename,
                   v_content     AS content,
                   v_language_id AS language_id,
                   v_created     AS created,
                   v_updated     AS updated
            FROM dual;
    END delete_file;
END snippet_pkg;
/