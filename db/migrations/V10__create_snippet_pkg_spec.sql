-------------------------------------------------------------------------
-- V10__create_snippet_pkg_spec.sql                                    --
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
    PROCEDURE get_paginated_snippets(
        p_user_id IN NUMBER,
        p_search_query IN VARCHAR2,
        p_tag_ids IN number_array,
        p_language_ids IN number_array,
        p_page_number IN NUMBER,
        p_page_size IN NUMBER,
        p_total_count OUT NUMBER,
        p_snippets OUT SYS_REFCURSOR
    );
    FUNCTION get_snippet(p_id IN NUMBER) RETURN SYS_REFCURSOR;
    PROCEDURE delete_snippet(p_id IN NUMBER, p_snippet OUT SYS_REFCURSOR);
    PROCEDURE update_snippet(
        p_id IN NUMBER,
        p_title IN VARCHAR2,
        p_description IN VARCHAR2,
        p_snippet OUT SYS_REFCURSOR
    );
    PROCEDURE add_tag_to_snippet(p_snippet_id IN NUMBER, p_tag_id IN NUMBER);
    PROCEDURE remove_tag_from_snippet(p_snippet_id IN NUMBER, p_tag_id IN NUMBER);
    PROCEDURE create_file(
        p_snippet_id IN NUMBER,
        p_filename IN VARCHAR2,
        p_content IN CLOB,
        p_language_id IN NUMBER,
        p_file OUT SYS_REFCURSOR
    );
    FUNCTION get_files_of_snippet(p_snippet_id IN NUMBER) RETURN SYS_REFCURSOR;
    FUNCTION get_file(p_id IN NUMBER) RETURN SYS_REFCURSOR;
    PROCEDURE update_file(
        p_id IN NUMBER,
        p_filename IN VARCHAR2 DEFAULT NULL,
        p_content IN CLOB DEFAULT NULL,
        p_language_id IN NUMBER DEFAULT NULL,
        p_file OUT SYS_REFCURSOR
    );
    PROCEDURE delete_file(p_id IN NUMBER, p_file OUT SYS_REFCURSOR);
END snippet_pkg;
/