-------------------------------------------------------------------------
-- V12__create_tag_pkg_spec.sql                                        --
-------------------------------------------------------------------------
-- Creates the tag package's specification                             --
-------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE tag_pkg AS
    -- tags --
    PROCEDURE create_tag(
        p_user_id IN NUMBER,
        p_name IN VARCHAR2,
        p_color IN VARCHAR2,
        p_tag OUT SYS_REFCURSOR
    );
    PROCEDURE update_tag(
        p_id IN NUMBER,
        p_name IN VARCHAR2 DEFAULT NULL,
        p_color IN VARCHAR2 DEFAULT NULL,
        p_tag OUT SYS_REFCURSOR
    );
    PROCEDURE delete_tag(p_id IN NUMBER, p_tag OUT SYS_REFCURSOR);
    FUNCTION get_tags(p_user_id IN NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION get_tag(p_id IN NUMBER) RETURN SYS_REFCURSOR;
END tag_pkg;
/