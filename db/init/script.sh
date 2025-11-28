#!/bin/bash

FLAG_FILE="/u01/app/oracle/oradata/db_initialized.flag"

echo "Starting init.sh..."

# Load credentials from env vars
DB_USER="${DB_USER:-snippetvault}"
DB_PASS="${DB_PASS:-secret}"

# Exit if the DB is already initialized
if [ -f "$FLAG_FILE" ]; then
    echo "Database already initialized. Skipping init."
    exit 0
fi


# Run the SQL commands
sqlplus -s sys/oracle as sysdba <<SQL
CREATE USER $DB_USER
IDENTIFIED BY "$DB_PASS"
DEFAULT TABLESPACE USERS
QUOTA UNLIMITED ON USERS;

GRANT CREATE SESSION TO $DB_USER;
GRANT CREATE TABLE TO $DB_USER;
GRANT CREATE VIEW TO $DB_USER;
GRANT CREATE SEQUENCE TO $DB_USER;
GRANT CREATE PROCEDURE TO $DB_USER;
GRANT CREATE TRIGGER TO $DB_USER;
GRANT CREATE TYPE TO $DB_USER;
GRANT EXECUTE ON CTXSYS.CTX_DDL TO $DB_USER;
GRANT EXECUTE ON CTXSYS.CTX_CLS TO $DB_USER;
GRANT EXECUTE ON CTXSYS.CTX_OUTPUT TO $DB_USER;
GRANT CREATE JOB TO $DB_USER;
SQL

# Create the flag file to indicate initialization is done
touch "$FLAG_FILE"

echo "Init script finished."
