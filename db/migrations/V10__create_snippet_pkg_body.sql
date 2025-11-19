-------------------------------------------------------------------------
-- V10__create_snippet_pkg_body.sql                                    --
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
END snippet_pkg;
/