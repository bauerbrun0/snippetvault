-------------------------------------------------------------------------
-- V10__create_snippet_search_view.sql                                 --
-------------------------------------------------------------------------
-- Creates the view helping the snippet search feature                 --
-------------------------------------------------------------------------

CREATE OR REPLACE VIEW vw_snippet_search AS
    SELECT s.id as id,
           s.user_id as user_id,
           s.title as title,
           s.description as description,
           s.created as created,
           s.updated as updated,
           f.filename as filename,
           f.id as file_id,
           f.content as content,
           f.language_id as language_id,
           st.tag_id as tag_id
    FROM snippet s
    LEFT JOIN snippetvault_file f ON f.snippet_id = s.id
    LEFT JOIN snippet_tag st ON st.snippet_id = s.id
/