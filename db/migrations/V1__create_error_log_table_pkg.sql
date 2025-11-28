-------------------------------------------------------------------------
-- V1__create_error_log_table_pkg.sql                                  --
-------------------------------------------------------------------------
-- Creates error_log table, sequence, trigger, and logging package     --
-- for centralized exception logging.                                  --
-------------------------------------------------------------------------


-- Sequence --

CREATE SEQUENCE error_log_seq
    START WITH 10000
    INCREMENT BY 1;

-- Table --

CREATE TABLE error_log
(
    id              NUMBER,
    time            TIMESTAMP DEFAULT SYSDATE,
    error_message   VARCHAR2(4000),
    error_backtrace VARCHAR2(4000),
    context         VARCHAR2(4000),
    value           VARCHAR2(4000),
    api             VARCHAR2(100)
);

-- Constraint --

ALTER TABLE error_log
    ADD CONSTRAINT pk_error_log PRIMARY KEY (id);

-- Trigger --

CREATE OR REPLACE TRIGGER trg_error_log_before_insert
    BEFORE INSERT
    ON error_log
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT error_log_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

-- Package --

CREATE OR REPLACE PACKAGE error_log_pkg AS
    PROCEDURE log_error(
        p_error_message VARCHAR2,
        p_error_backtrace VARCHAR2,
        p_context VARCHAR2,
        p_value VARCHAR2,
        p_api VARCHAR2
    );
END error_log_pkg;
/

CREATE OR REPLACE PACKAGE BODY error_log_pkg AS
    PROCEDURE log_error(
        p_error_message VARCHAR2,
        p_error_backtrace VARCHAR2,
        p_context VARCHAR2,
        p_value VARCHAR2,
        p_api VARCHAR2
    ) IS
        PRAGMA AUTONOMOUS_TRANSACTION;
    BEGIN
        INSERT INTO error_log (error_message, error_backtrace, context, value, api)
        VALUES (p_error_message, p_error_backtrace, p_context, p_value, p_api);
        COMMIT;
    END log_error;
END error_log_pkg;
/
