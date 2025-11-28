-------------------------------------------------------------------------
-- V3__insert_user_admin_roles.sql                                     --
-------------------------------------------------------------------------
-- Inserts the "USER" and "ADMIN" roles to the role table              --
-------------------------------------------------------------------------

INSERT INTO role (name) VALUES ('USER');
INSERT INTO role (name) VALUES ('ADMIN');

COMMIT;