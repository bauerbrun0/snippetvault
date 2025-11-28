-------------------------------------------------------------------------
-- V7__create_snippet_file_tag_tables.sql                              --
-------------------------------------------------------------------------
-- Creates SNIPPET, FILE, LANGUAGE, TAG, TAG_SNIPPET                   --
-- tables with sequences, constraints, triggers and indexes            --
-------------------------------------------------------------------------


---------------
-- Sequences --
---------------


CREATE SEQUENCE snippet_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE file_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE language_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE tag_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE tag_history_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE snippet_tag_seq
    START WITH 1
    INCREMENT BY 1;


------------
-- Tables --
------------


CREATE TABLE snippet (
    id NUMBER,
    user_id NUMBER NOT NULL,
    title VARCHAR2(255 CHAR) NOT NULL,
    description VARCHAR2(4000 CHAR) NOT NULL,
    created TIMESTAMP DEFAULT SYSDATE NOT NULL,
    updated TIMESTAMP DEFAULT SYSDATE NOT NULL
);

CREATE TABLE snippetvault_file (
    id NUMBER,
    snippet_id NUMBER NOT NULL,
    filename VARCHAR2(255 CHAR) NOT NULL,
    content CLOB NOT NULL,
    language_id NUMBER NOT NULL,
    created TIMESTAMP DEFAULT SYSDATE NOT NULL,
    updated TIMESTAMP DEFAULT SYSDATE NOT NULL
);

CREATE TABLE language (
    id NUMBER,
    name VARCHAR2(100 CHAR) NOT NULL
);

CREATE TABLE tag (
    id NUMBER,
    name VARCHAR2(150 CHAR) NOT NULL,
    user_id NUMBER NOT NULL,
    color VARCHAR2(7 CHAR) NOT NULL,
    created TIMESTAMP DEFAULT SYSDATE NOT NULL,
    version NUMBER NOT NULL
);

CREATE TABLE tag_history AS
    SELECT t.*,
           cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_action,
           cast(NULL AS TIMESTAMP) AS history_timestamp
    FROM tag t
    WHERE 1=0;

CREATE TABLE snippet_tag (
    id NUMBER,
    tag_id NUMBER NOT NULL,
    snippet_id NUMBER NOT NULL
);


-----------------
-- Constraints --
-----------------


ALTER TABLE snippet
    ADD CONSTRAINT pk_snippet PRIMARY KEY (id);
ALTER TABLE snippet
    ADD CONSTRAINT fk_snippet_user
    FOREIGN KEY (user_id) REFERENCES snippetvault_user (id)
    ON DELETE CASCADE;

ALTER TABLE language
    ADD CONSTRAINT pk_language PRIMARY KEY (id);
ALTER TABLE language
    ADD CONSTRAINT uq_language_name UNIQUE (name);

ALTER TABLE snippetvault_file
    ADD CONSTRAINT pk_file PRIMARY KEY (id);
ALTER TABLE snippetvault_file
    ADD CONSTRAINT fk_file_snippet
    FOREIGN KEY (snippet_id) REFERENCES snippet (id)
    ON DELETE CASCADE;
ALTER TABLE snippetvault_file
    ADD CONSTRAINT fk_file_language
    FOREIGN KEY (language_id) REFERENCES language (id)
    ON DELETE CASCADE;

ALTER TABLE tag
    ADD CONSTRAINT pk_tag PRIMARY KEY (id);
ALTER TABLE tag
    ADD CONSTRAINT fk_tag_user
    FOREIGN KEY (user_id) REFERENCES snippetvault_user (id)
    ON DELETE CASCADE;
ALTER TABLE tag ADD CONSTRAINT chk_tag_color
    CHECK (REGEXP_LIKE(color, '^#[0-9A-Fa-f]{6}$'));

ALTER TABLE tag_history
    ADD CONSTRAINT pk_tag_history PRIMARY KEY (history_id);

ALTER TABLE snippet_tag
    ADD CONSTRAINT pk_snippet_tag PRIMARY KEY (id);
ALTER TABLE snippet_tag
    ADD CONSTRAINT fk_snippet_tag_snippet
    FOREIGN KEY (snippet_id) REFERENCES snippet (id)
    ON DELETE CASCADE;
ALTER TABLE snippet_tag
    ADD CONSTRAINT fk_snippet_tag_tag
    FOREIGN KEY (tag_id) REFERENCES tag (id)
    ON DELETE CASCADE;
ALTER TABLE snippet_tag
    ADD CONSTRAINT uq_snippet_tag UNIQUE (snippet_id, tag_id);


--------------
-- Triggers --
--------------


CREATE OR REPLACE TRIGGER trg_snippet_before_insert
    BEFORE INSERT ON snippet
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT snippet_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_snippet_before_update
    BEFORE UPDATE ON snippet
    FOR EACH ROW
BEGIN
    IF :NEW.updated IS NULL THEN
        :NEW.updated := SYSDATE;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_file_before_insert
    BEFORE INSERT ON snippetvault_file
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT file_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_file_before_update
    BEFORE UPDATE ON snippetvault_file
    FOR EACH ROW
BEGIN
    IF :NEW.updated IS NULL THEN
        :NEW.updated := SYSDATE;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_file_after_update
    AFTER UPDATE ON snippetvault_file
    FOR EACH ROW
BEGIN
    UPDATE snippet s
    SET s.updated = SYSDATE
    WHERE s.id = :NEW.snippet_id;
END;
/

CREATE OR REPLACE TRIGGER trg_language_before_insert
    BEFORE INSERT ON language
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT language_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_tag_before_modify
    BEFORE INSERT OR UPDATE OR DELETE ON tag
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT tag_seq.nextval INTO :NEW.id FROM dual;
        END IF;
        :NEW.version := 1;
    ELSIF UPDATING THEN
        :NEW.version := :OLD.version + 1;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_tag_after_modify
    AFTER INSERT OR UPDATE OR DELETE ON tag
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO tag_history (
            id, name, user_id, color, created, version,
            history_id, history_action, history_timestamp
        )
        VALUES (
                   :NEW.id, :NEW.name, :NEW.user_id, :NEW.color,
                   :NEW.created, :NEW.version,
                   tag_history_seq.NEXTVAL, 'I', SYSDATE
               );
    ELSIF UPDATING THEN
        INSERT INTO tag_history (
            id, name, user_id, color, created, version,
            history_id, history_action, history_timestamp
        )
        VALUES (
                   :OLD.id, :OLD.name, :OLD.user_id, :OLD.color,
                   :OLD.created, :OLD.version,
                   tag_history_seq.NEXTVAL, 'U', SYSDATE
               );
    ELSIF DELETING THEN
        INSERT INTO tag_history (
            id, name, user_id, color, created, version,
            history_id, history_action, history_timestamp
        )
        VALUES (
                   :OLD.id, :OLD.name, :OLD.user_id, :OLD.color,
                   :OLD.created, :OLD.version,
                   tag_history_seq.NEXTVAL, 'D', SYSDATE
               );
    END IF;
END;
/


CREATE OR REPLACE TRIGGER trg_snippet_tag_before_insert
    BEFORE INSERT ON snippet_tag
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT snippet_tag_seq.nextval INTO :NEW.id FROM dual;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_snippet_tag_after_ins_del
    FOR INSERT OR DELETE ON snippet_tag
    COMPOUND TRIGGER
    ids number_array := number_array();
AFTER EACH ROW IS
BEGIN
    IF INSERTING THEN
        ids.EXTEND;
        ids(ids.LAST) := :NEW.snippet_id;
    ELSIF DELETING THEN
        ids.EXTEND;
        ids(ids.LAST) := :OLD.snippet_id;
    END IF;
END AFTER EACH ROW;

AFTER STATEMENT IS
BEGIN
    -- remove duplicates
    ids := set(ids);

    UPDATE snippet
    SET updated = SYSDATE
    WHERE id IN (SELECT COLUMN_VALUE FROM TABLE(ids));

END AFTER STATEMENT;
END trg_snippet_tag_after_ins_del;
/

-------------
-- Indexes --
-------------


CREATE INDEX idx_file_content
    ON snippetvault_file(content)
    INDEXTYPE IS CTXSYS.CONTEXT;
