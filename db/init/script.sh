#!/bin/bash

FLAG_FILE="/u01/app/oracle/oradata/db_initialized.flag"

echo "Starting init.sh..."

# Exit if the DB is already initialized
if [ -f "$FLAG_FILE" ]; then
    echo "Database already initialized. Skipping init."
    exit 0
fi


# Run the SQL commands
sqlplus -s sys/oracle as sysdba <<SQL
CREATE USER snippetvault
IDENTIFIED BY secret
DEFAULT TABLESPACE USERS
QUOTA UNLIMITED ON USERS;

GRANT CREATE SESSION TO snippetvault;
GRANT CREATE TABLE TO snippetvault;
GRANT CREATE VIEW TO snippetvault;
GRANT CREATE SEQUENCE TO snippetvault;
GRANT CREATE PROCEDURE TO snippetvault;
GRANT CREATE TRIGGER TO snippetvault;
GRANT CREATE TYPE TO snippetvault;
GRANT EXECUTE ON CTXSYS.CTX_DDL TO snippetvault;
GRANT EXECUTE ON CTXSYS.CTX_CLS TO snippetvault;
GRANT EXECUTE ON CTXSYS.CTX_OUTPUT TO snippetvault;
SQL

# Create the flag file to indicate initialization is done
touch "$FLAG_FILE"

echo "Init script finished."
