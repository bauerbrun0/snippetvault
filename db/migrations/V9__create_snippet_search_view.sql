-------------------------------------------------------------------------
-- V9__create_snippet_search_view.sql                                  --
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
           f.content as content,
           f.language_id as language_id,
           l.name as language_name,
           t.id as tag_id,
           t.name as tag_name,
           t.color as tag_color
    FROM snippet s
    LEFT JOIN snippetvault_file f ON f.snippet_id = s.id
    LEFT JOIN language l ON f.language_id = l.id
    LEFT JOIN snippet_tag st ON st.snippet_id = s.id
    LEFT JOIN tag t ON st.tag_id = t.id;
/