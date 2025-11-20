-------------------------------------------------------------------------
-- V5__create_types.sql                                                --
-------------------------------------------------------------------------
-- Creates custom types                                                --
-------------------------------------------------------------------------

CREATE OR REPLACE TYPE role_array AS TABLE OF VARCHAR2(50 CHAR);
/
CREATE OR REPLACE TYPE number_array AS TABLE OF NUMBER;
/