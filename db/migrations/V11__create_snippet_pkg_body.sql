-------------------------------------------------------------------------
-- V11__create_snippet_pkg_body.sql                                    --
-------------------------------------------------------------------------
-- Creates the snippet package's body                                  --
-------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE BODY snippet_pkg AS
    -- snippets --
    PROCEDURE create_snippet(
        p_user_id IN NUMBER,
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
            SELECT v_id AS id,
                   v_user_id AS user_id,
                   v_title AS title,
                   v_description AS description,
                   v_created AS created,
                   v_updated AS updated
            FROM dual;
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_SNIPPET_USER%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
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
    BEGIN
        NULL;
    END get_paginated_snippets;
    -- tags --
    PROCEDURE create_tag(
        p_user_id IN NUMBER,
        p_name IN VARCHAR2,
        p_color IN VARCHAR2,
        p_tag OUT SYS_REFCURSOR
    )AS
        v_id NUMBER;
    BEGIN
        INSERT INTO tag (user_id, name, color)
        VALUES (p_user_id, p_name, p_color)
        RETURNING id INTO v_id;

        OPEN p_tag FOR
            SELECT t.id as id,
                   t.user_id as user_id,
                   t.name as name,
                   t.color as color,
                   t.created as created
            FROM tag t
            WHERE t.id = v_id;

    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_SNIPPET_USER%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
                END IF;
            ELSIF SQLCODE = -2290 THEN
                IF SQLERRM LIKE '%CHK_TAG_COLOR%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_COLOR_INVALID, 'Tag color must be a valid hex code');
                END IF;
            ELSE
                RAISE;
            END IF;
    END create_tag;
END snippet_pkg;
/