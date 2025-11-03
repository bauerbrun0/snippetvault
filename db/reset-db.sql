-------------------------------------------------------------------------
-- reset-db.sql                                                        --
-------------------------------------------------------------------------
-- A down script for all of the migrations for development             --
-------------------------------------------------------------------------

---------------------------------
-- V1__create_user_role_tables --
---------------------------------


DROP TABLE user_role;
DROP TABLE role;
DROP TABLE snippetvault_user;

DROP SEQUENCE user_seq;
DROP SEQUENCE role_seq;
DROP SEQUENCE user_role_seq;



-------------------------------------------
-- lastly, drop the flyway history table --
-------------------------------------------


DROP TABLE "flyway_schema_history";