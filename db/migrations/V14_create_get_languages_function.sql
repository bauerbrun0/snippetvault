-------------------------------------------------------------------------
-- V14__create_get_languages_function.sql                              --
-------------------------------------------------------------------------
-- Creates the get_languages_function to query all                     --
-- available languages                                                 --
-------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION get_languages RETURN SYS_REFCURSOR AS
    languages_cursor SYS_REFCURSOR;
BEGIN
    OPEN languages_cursor FOR
        SELECT l.id,
               l.name
        FROM language l;

    RETURN languages_cursor;
END get_languages_function;