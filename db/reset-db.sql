-------------------------------------------------------------------------
-- reset-db.sql                                                        --
-------------------------------------------------------------------------
-- A down script for all of the migrations for development             --
-------------------------------------------------------------------------


------------------------------
-- V6__create_user_pkg_body --
-- V5__create_user_pkg_spec --
------------------------------


DROP PACKAGE user_pkg;


------------------------------
-- V4__create_constants_pkg --
------------------------------


DROP PACKAGE constants_pkg;

----------------------
-- V3__create_types --
----------------------


DROP type role_arr;


---------------------------------
-- V2__insert_user_admin_roles --
-- V1__create_user_role_tables --
----------------------------------


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