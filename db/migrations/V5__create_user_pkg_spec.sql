-------------------------------------------------------------------------
-- V5__create_user_pkg_spec.sql                                        --
-------------------------------------------------------------------------
-- Creates the user package's specification                            --
-------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE user_pkg AS
    PROCEDURE create_user(
        p_username IN VARCHAR2,
        p_password_hash IN VARCHAR2,
        p_roles IN role_array,
        p_user OUT SYS_REFCURSOR
    );
    FUNCTION get_users RETURN SYS_REFCURSOR;
    FUNCTION get_user(p_id IN NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION get_user_by_username(p_username IN VARCHAR2) RETURN SYS_REFCURSOR;
    PROCEDURE delete_user(p_id IN NUMBER, p_user OUT SYS_REFCURSOR);
    PROCEDURE update_user(
        p_id IN NUMBER,
        p_username IN VARCHAR2 DEFAULT NULL,
        p_password_hash IN VARCHAR2 DEFAULT NULL,
        p_user OUT SYS_REFCURSOR
    );
    FUNCTION get_user_roles(p_id IN NUMBER) RETURN SYS_REFCURSOR;
END user_pkg;
/