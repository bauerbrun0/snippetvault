-------------------------------------------------------------------------
-- V9__create_snippet_pkg_spec.sql                                     --
-------------------------------------------------------------------------
-- Creates the snippet package's specification                         --
-------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE snippet_pkg AS
    PROCEDURE create_snippet(
        p_user_id IN NUMBER,
        p_title IN VARCHAR2,
        p_description IN VARCHAR2,
        p_snippet OUT SYS_REFCURSOR
    );
END snippet_pkg;
/