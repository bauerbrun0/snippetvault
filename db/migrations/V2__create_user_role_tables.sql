-------------------------------------------------------------------------
-- V2__create_user_role_tables.sql                                     --
-------------------------------------------------------------------------
-- Creates USER, ROLE, USER_ROLE tables with sequences,                --
-- constraints, triggers and history tables                            --
-------------------------------------------------------------------------


---------------
-- Sequences --
---------------


CREATE SEQUENCE user_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE snippetvault_user_history_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE role_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE role_history_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE user_role_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE user_role_history_seq
    START WITH 10000
    INCREMENT BY 1;


------------
-- Tables --
------------


CREATE TABLE snippetvault_user(
    id            NUMBER,
    username      VARCHAR2(50 CHAR)              NOT NULL,
    password_hash VARCHAR2(255 CHAR)             NOT NULL,
    created       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag        VARCHAR2(1)                    NOT NULL,
    version       NUMBER                         NOT NULL
);

CREATE TABLE snippetvault_user_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           u.*
    FROM snippetvault_user u WHERE 1=0;


CREATE TABLE role(
    id      NUMBER,
    name    VARCHAR2(50 CHAR)              NOT NULL,
    created TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag  VARCHAR2(1)                    NOT NULL,
    version NUMBER                         NOT NULL
);

CREATE TABLE role_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           r.*
    FROM role r WHERE 1=0;


CREATE TABLE user_role(
    id      NUMBER,
    user_id NUMBER                         NOT NULL,
    role_id NUMBER                         NOT NULL,
    created TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag  VARCHAR2(1)                    NOT NULL,
    version NUMBER                         NOT NULL
);

CREATE TABLE user_role_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           ur.*
    FROM user_role ur WHERE 1=0;


-----------------
-- Constraints --
-----------------


ALTER TABLE snippetvault_user
    ADD CONSTRAINT pk_user PRIMARY KEY (id);
ALTER TABLE snippetvault_user
    ADD CONSTRAINT uq_user_username UNIQUE (username);

ALTER TABLE snippetvault_user_history
    ADD CONSTRAINT pk_user_history PRIMARY KEY (history_id);

ALTER TABLE role
    ADD CONSTRAINT pk_role PRIMARY KEY (id);
ALTER TABLE role
    ADD CONSTRAINT uq_role_name UNIQUE (name);

ALTER TABLE role_history
    ADD CONSTRAINT pk_role_history PRIMARY KEY (history_id);

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

ALTER TABLE user_role_history
    ADD CONSTRAINT pk_user_role_history PRIMARY KEY (history_id);

--------------
-- Triggers --
--------------


CREATE OR REPLACE TRIGGER trg_user_before_insert_update
    BEFORE INSERT OR UPDATE ON snippetvault_user
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT user_seq.nextval INTO :NEW.id FROM dual;
        END IF;
        :NEW.created := SYSTIMESTAMP;
        :NEW.updated := SYSTIMESTAMP;
        :NEW.version := 1;
        :NEW.dml_flag := 'I';
    ELSIF UPDATING THEN
        :NEW.updated := SYSTIMESTAMP;
        :NEW.version := NVL(:OLD.version, 0) + 1;
        :NEW.dml_flag := 'U';
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_user_history_after_dml
    AFTER INSERT OR UPDATE OR DELETE
    ON snippetvault_user
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO snippetvault_user_history (
            history_id, history_dml_flag, history_timestamp,
            id, username, password_hash, created, updated, dml_flag, version
        )
        VALUES (
            snippetvault_user_history_seq.nextval, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.username, :OLD.password_hash, :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO snippetvault_user_history (
            history_id, history_dml_flag, history_timestamp,
            id, username, password_hash, created, updated, dml_flag, version
        )
        VALUES (
            snippetvault_user_history_seq.nextval, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.username, :NEW.password_hash, :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_role_before_insert_update
    BEFORE INSERT OR UPDATE ON role
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT role_seq.nextval INTO :NEW.id FROM dual;
        END IF;
        :NEW.created := SYSTIMESTAMP;
        :NEW.updated := SYSTIMESTAMP;
        :NEW.version := 1;
        :NEW.dml_flag := 'I';
    ELSIF UPDATING THEN
        :NEW.updated := SYSTIMESTAMP;
        :NEW.version := NVL(:OLD.version, 0) + 1;
        :NEW.dml_flag := 'U';
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_role_history_after_dml
    AFTER INSERT OR UPDATE OR DELETE
    ON role
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO role_history (
            history_id, history_dml_flag, history_timestamp,
            id, name, created, updated, dml_flag, version
        )
        VALUES (
            role_history_seq.nextval, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.name, :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO role_history (
            history_id, history_dml_flag, history_timestamp,
            id, name, created, updated, dml_flag, version
        )
        VALUES (
            role_history_seq.nextval, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.name, :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_user_role_before_ins_upd
    BEFORE INSERT OR UPDATE ON user_role
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT user_role_seq.nextval INTO :NEW.id FROM dual;
        END IF;
        :NEW.created := SYSTIMESTAMP;
        :NEW.updated := SYSTIMESTAMP;
        :NEW.version := 1;
        :NEW.dml_flag := 'I';
    ELSIF UPDATING THEN
        :NEW.updated := SYSTIMESTAMP;
        :NEW.version := NVL(:OLD.version, 0) + 1;
        :NEW.dml_flag := 'U';
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_user_role_hist_after_dml
    AFTER INSERT OR UPDATE OR DELETE
    ON user_role
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO user_role_history (
            history_id, history_dml_flag, history_timestamp,
            id, user_id, role_id, created, updated, dml_flag, version
        )
        VALUES (
            user_role_history_seq.nextval, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.user_id, :OLD.role_id, :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO user_role_history (
            history_id, history_dml_flag, history_timestamp,
            id, user_id, role_id, created, updated, dml_flag, version
        )
        VALUES (
            user_role_history_seq.nextval, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.user_id, :NEW.role_id, :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/
