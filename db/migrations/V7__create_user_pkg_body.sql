-------------------------------------------------------------------------
-- V7__create_user_pkg_body.sql                                        --
-------------------------------------------------------------------------
-- Creates the user package's body                                     --
-------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE BODY user_pkg AS
    PROCEDURE create_user(
        p_username IN VARCHAR2,
        p_password_hash IN VARCHAR2,
        p_roles IN role_array,
        p_user OUT SYS_REFCURSOR
    ) AS
        v_user_id NUMBER;
        v_username VARCHAR2(50 CHAR);
        v_password_hash VARCHAR2(255 CHAR);
        v_created TIMESTAMP;
        v_current_role_id NUMBER;
    BEGIN
        INSERT INTO snippetvault_user (username, password_hash)
        VALUES (p_username, p_password_hash)
        RETURNING id,
            username,
            password_hash,
            created
        INTO v_user_id,
            v_username,
            v_password_hash,
            v_created;

        FOR i IN 1 .. p_roles.COUNT LOOP
            BEGIN
                SELECT id INTO v_current_role_id FROM role WHERE name = p_roles(i);
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    RAISE_APPLICATION_ERROR(
                            constants_pkg.ERR_ROLE_NOT_FOUND,
                            'Role "' || p_roles(i) || '" does not exist.'
                    );
            END;

            INSERT INTO user_role (user_id, role_id)
            VALUES (v_user_id, v_current_role_id);
        END LOOP;

        OPEN p_user FOR
            SELECT v_user_id AS id,
                   v_username AS username,
                   v_password_hash AS password_hash,
                   v_created AS created
            FROM dual;
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            IF SQLERRM LIKE '%UQ_USER_USERNAME%' THEN
                RAISE_APPLICATION_ERROR(constants_pkg.ERR_DUPLICATE_USERNAME, 'Username already taken');
            ELSE
                RAISE;
            END IF;
        WHEN OTHERS THEN
            RAISE;
    END create_user;

    FUNCTION get_users RETURN SYS_REFCURSOR AS
        v_cur SYS_REFCURSOR;
    BEGIN
        OPEN v_cur FOR
            SELECT id,
                   username,
                   password_hash,
                   created
            FROM snippetvault_user
            ORDER BY created;

        RETURN v_cur;
    END get_users;

    FUNCTION get_user(p_id IN NUMBER) RETURN SYS_REFCURSOR AS
        v_cur SYS_REFCURSOR;
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM snippetvault_user
        WHERE id = p_id;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
        END IF;

        OPEN v_cur FOR
            SELECT id,
                   username,
                   password_hash,
                   created
            FROM snippetvault_user WHERE id = p_id;
        RETURN v_cur;
    END get_user;

    FUNCTION get_user_by_username(p_username IN VARCHAR2) RETURN SYS_REFCURSOR AS
        v_count NUMBER;
        v_cur SYS_REFCURSOR;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM snippetvault_user
        WHERE username = p_username;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
        END IF;

        OPEN v_cur FOR
            SELECT id,
                   username,
                   password_hash,
                   created
            FROM snippetvault_user WHERE username = p_username;
        RETURN v_cur;
    END get_user_by_username;

    PROCEDURE delete_user(p_id IN NUMBER, p_user OUT SYS_REFCURSOR) AS
        v_is_admin NUMBER;
        v_admin_count NUMBER;
        v_id NUMBER;
        v_username VARCHAR2(50 CHAR);
        v_password_hash VARCHAR2(255 CHAR);
        v_created TIMESTAMP;
    BEGIN
        SELECT COUNT(*)
        INTO v_is_admin
        FROM user_role ur
                 JOIN role r ON ur.role_id = r.id
        WHERE ur.user_id = p_id
          AND r.name = 'ADMIN';

        IF v_is_admin = 1 THEN
            SELECT COUNT(*)
            INTO v_admin_count
            FROM user_role ur
                     JOIN role r ON ur.role_id = r.id
            WHERE r.name = 'ADMIN';

            IF v_admin_count <= 1 THEN
                RAISE_APPLICATION_ERROR(
                        constants_pkg.ERR_CANNOT_DELETE_LAST_ADMIN,
                        'Cannot delete the last admin user'
                );
            END IF;
        END IF;

        DELETE FROM snippetvault_user WHERE id = p_id
        RETURNING id,
            username,
            password_hash,
            created
        INTO v_id,
            v_username,
            v_password_hash,
            v_created;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
        END IF;

        OPEN p_user FOR
            SELECT v_id AS id,
                   v_username AS username,
                   v_password_hash AS password_hash,
                   v_created AS created
            FROM dual;
    END delete_user;

    PROCEDURE update_user(
        p_id IN NUMBER,
        p_username IN VARCHAR2 DEFAULT NULL,
        p_password_hash IN VARCHAR2 DEFAULT NULL,
        p_user OUT SYS_REFCURSOR
    ) AS
        v_id NUMBER;
        v_username VARCHAR2(50 CHAR);
        v_password_hash VARCHAR2(255 CHAR);
        v_created TIMESTAMP;
    BEGIN
        UPDATE snippetvault_user
            SET username = COALESCE(p_username, username),
                password_hash = COALESCE(p_password_hash, password_hash)
            WHERE id = p_id
            RETURNING id,
                username,
                password_hash,
                created
            INTO v_id,
                v_username,
                v_password_hash,
                v_created;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(constants_pkg.ERR_USER_NOT_FOUND, 'User not found');
        END IF;

        OPEN p_user FOR
            SELECT v_id AS id,
                   v_username AS username,
                   v_password_hash AS password_hash,
                   v_created AS created
            FROM dual;

    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            IF SQLERRM LIKE '%UQ_USER_USERNAME%' THEN
                RAISE_APPLICATION_ERROR(constants_pkg.ERR_DUPLICATE_USERNAME, 'Username already taken');
            ELSE
                RAISE;
            END IF;
        WHEN OTHERS THEN
            RAISE;
    END update_user;

    FUNCTION get_user_roles(p_id IN NUMBER) RETURN SYS_REFCURSOR AS
        v_cur SYS_REFCURSOR;
    BEGIN
        OPEN v_cur FOR
            SELECT r.name AS name, r.ID AS id
            FROM role r
            JOIN user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = p_id
            ORDER BY r.name;

        return v_cur;
    END get_user_roles;
END user_pkg;
/