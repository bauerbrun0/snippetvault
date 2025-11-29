-------------------------------------------------------------------------
-- V8__create_snippet_file_tag_tables.sql                              --
-------------------------------------------------------------------------
-- Creates SNIPPET, FILE, LANGUAGE, TAG, TAG_SNIPPET                   --
-- tables with sequences, constraints, triggers, indexes and           --
-- history tables                                                      --
-------------------------------------------------------------------------


---------------
-- Sequences --
---------------


CREATE SEQUENCE snippet_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE snippet_history_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE file_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE file_history_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE language_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE language_history_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE tag_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE tag_history_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE snippet_tag_seq
    START WITH 10000
    INCREMENT BY 1;

CREATE SEQUENCE snippet_tag_history_seq
    START WITH 10000
    INCREMENT BY 1;


------------
-- Tables --
------------


CREATE TABLE snippet (
    id          NUMBER,
    user_id     NUMBER                         NOT NULL,
    title       VARCHAR2(255 CHAR)             NOT NULL,
    description VARCHAR2(4000 CHAR)            NOT NULL,
    created     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag    VARCHAR2(1)                    NOT NULL,
    version     NUMBER                         NOT NULL
);

CREATE TABLE snippet_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           s.*
    FROM snippet s WHERE 1=0;


CREATE TABLE snippetvault_file (
    id          NUMBER,
    snippet_id  NUMBER                         NOT NULL,
    filename    VARCHAR2(255 CHAR)             NOT NULL,
    content     CLOB                           NOT NULL,
    language_id NUMBER                         NOT NULL,
    created     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag    VARCHAR2(1)                    NOT NULL,
    version     NUMBER                         NOT NULL
);

CREATE TABLE snippetvault_file_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           f.*
    FROM snippetvault_file f WHERE 1=0;


CREATE TABLE language (
    id       NUMBER,
    name     VARCHAR2(100 CHAR)             NOT NULL,
    created  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag VARCHAR2(1)                    NOT NULL,
    version  NUMBER                         NOT NULL
);

CREATE TABLE language_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           l.*
    FROM language l WHERE 1=0;


CREATE TABLE tag (
    id       NUMBER,
    name     VARCHAR2(150 CHAR)             NOT NULL,
    user_id  NUMBER                         NOT NULL,
    color    VARCHAR2(7 CHAR)               NOT NULL,
    created  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag VARCHAR2(1)                    NOT NULL,
    version  NUMBER                         NOT NULL
);

CREATE TABLE tag_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           t.*
    FROM tag t WHERE 1=0;


CREATE TABLE snippet_tag (
    id         NUMBER,
    tag_id     NUMBER                         NOT NULL,
    snippet_id NUMBER                         NOT NULL,
    created    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    dml_flag   VARCHAR2(1)                    NOT NULL,
    version    NUMBER                         NOT NULL
);

CREATE TABLE snippet_tag_history AS
    SELECT cast(NULL AS NUMBER) AS history_id,
           cast(NULL AS VARCHAR2(1)) AS history_dml_flag,
           cast(NULL AS TIMESTAMP) AS history_timestamp,
           st.*
    FROM snippet_tag st WHERE 1=0;


-----------------
-- Constraints --
-----------------


ALTER TABLE snippet
    ADD CONSTRAINT pk_snippet PRIMARY KEY (id);
ALTER TABLE snippet
    ADD CONSTRAINT fk_snippet_user
    FOREIGN KEY (user_id) REFERENCES snippetvault_user (id)
    ON DELETE CASCADE;

ALTER TABLE snippet_history
    ADD CONSTRAINT pk_snippet_history PRIMARY KEY (history_id);

ALTER TABLE language
    ADD CONSTRAINT pk_language PRIMARY KEY (id);
ALTER TABLE language
    ADD CONSTRAINT uq_language_name UNIQUE (name);

ALTER TABLE language_history
    ADD CONSTRAINT pk_language_history PRIMARY KEY (history_id);

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

ALTER TABLE snippetvault_file_history
    ADD CONSTRAINT pk_file_history PRIMARY KEY (history_id);

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

ALTER TABLE snippet_tag_history
    ADD CONSTRAINT pk_snippet_tag_history PRIMARY KEY (history_id);


--------------
-- Triggers --
--------------


CREATE OR REPLACE TRIGGER trg_snippet_before_ins_upd
    BEFORE INSERT OR UPDATE ON snippet
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT snippet_seq.nextval INTO :NEW.id FROM dual;
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

CREATE OR REPLACE TRIGGER trg_snippet_after_dml
    AFTER INSERT OR UPDATE OR DELETE ON snippet
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO snippet_history (
            history_id, history_dml_flag, history_timestamp,
            id, user_id, title, description, created, updated, dml_flag, version
        )
        VALUES (
            snippet_history_seq.NEXTVAL, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.user_id, :OLD.title, :OLD.description, :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO snippet_history (
            history_id, history_dml_flag, history_timestamp,
            id, user_id, title, description, created, updated, dml_flag, version
        )
        VALUES (
            snippet_history_seq.NEXTVAL, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.user_id, :NEW.title, :NEW.description, :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_file_before_insert_update
    BEFORE INSERT OR UPDATE ON snippetvault_file
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT file_seq.nextval INTO :NEW.id FROM dual;
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

CREATE OR REPLACE TRIGGER trg_file_after_dml
    AFTER INSERT OR UPDATE OR DELETE ON snippetvault_file
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO snippetvault_file_history (
            history_id, history_dml_flag, history_timestamp,
            id, snippet_id, filename, content, language_id,
            created, updated, dml_flag, version
        )
        VALUES (
            file_history_seq.NEXTVAL, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.snippet_id, :OLD.filename, :OLD.content, :OLD.language_id,
            :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO snippetvault_file_history (
            history_id, history_dml_flag, history_timestamp,
            id, snippet_id, filename, content, language_id,
            created, updated, dml_flag, version
        )
        VALUES (
            file_history_seq.NEXTVAL, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.snippet_id, :NEW.filename, :NEW.content, :NEW.language_id,
            :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
        IF UPDATING THEN
            UPDATE snippet
            SET updated = SYSTIMESTAMP
            WHERE id = :NEW.snippet_id;
        END IF;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_language_before_ins_upd
    BEFORE INSERT OR UPDATE ON language
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT language_seq.nextval INTO :NEW.id FROM dual;
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

CREATE OR REPLACE TRIGGER trg_language_after_dml
    AFTER INSERT OR UPDATE OR DELETE ON language
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO language_history (
            history_id, history_dml_flag, history_timestamp,
            id, name, created, updated, dml_flag, version
        )
        VALUES (
            language_history_seq.NEXTVAL, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.name, :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO language_history (
            history_id, history_dml_flag, history_timestamp,
            id, name, created, updated, dml_flag, version
        )
        VALUES (
            language_history_seq.NEXTVAL, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.name, :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_tag_before_insert_update
    BEFORE INSERT OR UPDATE ON tag
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT tag_seq.nextval INTO :NEW.id FROM dual;
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

CREATE OR REPLACE TRIGGER trg_tag_after_dml
    AFTER INSERT OR UPDATE OR DELETE ON tag
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO tag_history (
            history_id, history_dml_flag, history_timestamp,
            id, name, user_id, color, created, updated, dml_flag, version
        )
        VALUES (
            tag_history_seq.NEXTVAL, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.name, :OLD.user_id, :OLD.color,
            :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO tag_history (
            history_id, history_dml_flag, history_timestamp,
            id, name, user_id, color, created, updated, dml_flag, version
        )
        VALUES (
            tag_history_seq.NEXTVAL, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.name, :NEW.user_id, :NEW.color,
            :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_snippet_tag_before_ins_upd
    BEFORE INSERT OR UPDATE ON snippet_tag
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT snippet_tag_seq.nextval INTO :NEW.id FROM dual;
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

CREATE OR REPLACE TRIGGER trg_snippet_tag_after_dml
    AFTER INSERT OR UPDATE OR DELETE ON snippet_tag
    FOR EACH ROW
BEGIN
    IF DELETING THEN
        INSERT INTO snippet_tag_history (
            history_id, history_dml_flag, history_timestamp,
            id, tag_id, snippet_id, created, updated, dml_flag, version
        )
        VALUES (
            snippet_tag_history_seq.NEXTVAL, 'D', SYSTIMESTAMP,
            :OLD.id, :OLD.tag_id, :OLD.snippet_id,
            :OLD.created, :OLD.updated, :OLD.dml_flag, :OLD.version
        );
    ELSE
        INSERT INTO snippet_tag_history (
            history_id, history_dml_flag, history_timestamp,
            id, tag_id, snippet_id, created, updated, dml_flag, version
        )
        VALUES (
            snippet_tag_history_seq.NEXTVAL, :NEW.dml_flag, SYSTIMESTAMP,
            :NEW.id, :NEW.tag_id, :NEW.snippet_id,
            :NEW.created, :NEW.updated, :NEW.dml_flag, :NEW.version
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_snippet_tag_for_ins_del
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
    SET updated = SYSTIMESTAMP
    WHERE id IN (SELECT COLUMN_VALUE FROM TABLE(ids));

END AFTER STATEMENT;
END trg_snippet_tag_for_ins_del;
/

-------------
-- Indexes --
-------------


CREATE INDEX idx_file_content
    ON snippetvault_file(content)
    INDEXTYPE IS CTXSYS.CONTEXT;
