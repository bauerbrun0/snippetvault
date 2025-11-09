----------------------------------------------------------------------------------------
-- V8__insert_languages.sql                                                           --
----------------------------------------------------------------------------------------
-- Inserts languages supported by prism-code-editor                                   --
-- https://github.com/jonpyt/prism-code-editor/tree/main/package/src/prism/languages  --
----------------------------------------------------------------------------------------

INSERT INTO language (name) VALUES ('java');
INSERT INTO language (name) VALUES ('javascript');
INSERT INTO language (name) VALUES ('typescript');
INSERT INTO language (name) VALUES ('xml');
INSERT INTO language (name) VALUES ('json');
INSERT INTO language (name) VALUES ('go');
INSERT INTO language (name) VALUES ('python');
INSERT INTO language (name) VALUES ('vue');
INSERT INTO language (name) VALUES ('c');
INSERT INTO language (name) VALUES ('cpp');
INSERT INTO language (name) VALUES ('bash');
INSERT INTO language (name) VALUES ('yaml');

-- TODO: write a script to fetch all the languages supported by prism-code-editor and finish the inserts

COMMIT;