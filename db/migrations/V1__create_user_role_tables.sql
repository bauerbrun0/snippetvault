-------------------------------------------------------------------------
-- V1__create_user_role_tables.sql                                     --
-------------------------------------------------------------------------
-- Creates USER, ROLE, USER_ROLE tables with sequences,                --
-- constraints and triggers                                            --
-------------------------------------------------------------------------


---------------
-- Sequences --
---------------


CREATE SEQUENCE user_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE role_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE user_role_seq
    START WITH 1
    INCREMENT BY 1;


------------
-- Tables --
------------


CREATE TABLE snippetvault_user (
    id NUMBER,
    username VARCHAR2(50 CHAR) NOT NULL,
    password_hash VARCHAR2(255 CHAR) NOT NULL,
    created TIMESTAMP DEFAULT SYSDATE NOT NULL
);

CREATE TABLE role (
    id NUMBER,
    name VARCHAR2(50 CHAR) NOT NULL
);

CREATE TABLE user_role (
    id NUMBER,
    user_id NUMBER NOT NULL,
    role_id NUMBER NOT NULL
);


-----------------
-- Constraints --
-----------------


ALTER TABLE snippetvault_user
    ADD CONSTRAINT pk_user PRIMARY KEY (id);
ALTER TABLE snippetvault_user
    ADD CONSTRAINT uq_user_username UNIQUE (username);

ALTER TABLE role
    ADD CONSTRAINT pk_role PRIMARY KEY (id);
ALTER TABLE role
    ADD CONSTRAINT uq_role_name UNIQUE (name);

ALTER TABLE user_role
    ADD CONSTRAINT pk_user_role PRIMARY KEY (id);
ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_user
    FOREIGN KEY (user_id) REFERENCES snippetvault_user (id)
    ON DELETE CASCADE;
ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role (id);
ALTER TABLE user_role
    ADD CONSTRAINT uq_user_role_user_role UNIQUE (user_id, role_id);


--------------
-- Triggers --
--------------


CREATE OR REPLACE TRIGGER trg_user_before_insert
    BEFORE INSERT ON snippetvault_user
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT user_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_role_before_insert
    BEFORE INSERT ON role
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT role_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_user_role_before_insert
    BEFORE INSERT ON user_role
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT user_role_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/