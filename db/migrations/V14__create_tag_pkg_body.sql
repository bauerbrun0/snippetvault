-------------------------------------------------------------------------
-- V14__create_tag_pkg_body.sql                                        --
-------------------------------------------------------------------------
-- Creates the tag package's body                                      --
-------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE BODY tag_pkg AS
    PROCEDURE create_tag(
        p_user_id IN NUMBER,
        p_name IN VARCHAR2,
        p_color IN VARCHAR2,
        p_tag OUT SYS_REFCURSOR
    ) AS
        v_id NUMBER;
    BEGIN
        INSERT INTO tag (user_id, name, color)
        VALUES (p_user_id, p_name, p_color)
        RETURNING id INTO v_id;

        OPEN p_tag FOR
            SELECT t.id      as id,
                   t.user_id as user_id,
                   t.name    as name,
                   t.color   as color,
                   t.created as created
            FROM tag t
            WHERE t.id = v_id;

    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2291 THEN
                IF SQLERRM LIKE '%FK_SNIPPET_USER%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
                ELSE
                    RAISE;
                END IF;
            ELSIF SQLCODE = -2290 THEN
                IF SQLERRM LIKE '%CHK_TAG_COLOR%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_COLOR_INVALID, 'Tag color must be a valid hex code');
                ELSE
                    RAISE;
                END IF;
            ELSE
                RAISE;
            END IF;
    END create_tag;

    PROCEDURE update_tag(
        p_id IN NUMBER,
        p_name IN VARCHAR2 DEFAULT NULL,
        p_color IN VARCHAR2 DEFAULT NULL,
        p_tag OUT SYS_REFCURSOR
    ) AS
        v_id NUMBER;
    BEGIN
        UPDATE tag
        SET name  = COALESCE(p_name, name),
            color = COALESCE(p_color, color)
        WHERE id = p_id
        RETURNING id INTO v_id;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_NOT_FOUND, 'Tag not found');
        END IF;

        OPEN p_tag FOR
            SELECT t.id      AS id,
                   t.user_id AS user_id,
                   t.name    AS name,
                   t.color   AS color,
                   t.created AS created
            FROM tag t
            WHERE id = v_id;

    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -2290 THEN
                IF SQLERRM LIKE '%CHK_TAG_COLOR%' THEN
                    RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_COLOR_INVALID, 'Tag color must be a valid hex code');
                ELSE
                    RAISE;
                END IF;
            ELSE
                RAISE;
            END IF;
    END update_tag;

    PROCEDURE delete_tag(p_id IN NUMBER, p_tag OUT SYS_REFCURSOR) AS
        v_id      NUMBER;
        v_user_id NUMBER;
        v_name    VARCHAR2(150 CHAR);
        v_color   VARCHAR2(7 CHAR);
        v_created TIMESTAMP;
    BEGIN
        DELETE
        FROM tag
        WHERE id = p_id
        RETURNING id,
            user_id,
            name,
            color,
            created
        INTO v_id,
            v_user_id,
            v_name,
            v_color,
            v_created;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_NOT_FOUND, 'Tag not found');
        END IF;

        OPEN p_tag FOR
            SELECT v_id      AS id,
                   v_user_id AS user_id,
                   v_name    AS name,
                   v_color   AS color,
                   v_created AS created
            FROM dual;
    END delete_tag;

    FUNCTION get_tags(p_user_id IN NUMBER) RETURN SYS_REFCURSOR AS
        v_cur SYS_REFCURSOR;
    BEGIN
        OPEN v_cur FOR
            SELECT id,
                   user_id,
                   name,
                   color,
                   created
            FROM tag
            WHERE user_id = p_user_id
            ORDER BY created;

        RETURN v_cur;
    END get_tags;

    FUNCTION get_tag(p_id IN NUMBER) RETURN SYS_REFCURSOR AS
        v_cur   SYS_REFCURSOR;
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM tag
        WHERE id = p_id;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_TAG_NOT_FOUND, 'Tag not found');
        END IF;

        OPEN v_cur FOR
            SELECT id,
                   user_id,
                   name,
                   color,
                   created
            FROM tag
            WHERE id = p_id;

        RETURN v_cur;
    END get_tag;
END tag_pkg;
/