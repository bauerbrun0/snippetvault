-------------------------------------------------------------------------
-- V4__create_constants_pkg.sql                                        --
-------------------------------------------------------------------------
-- Creates constants package where constants                           --
-- like error codes are stored                                         --
-------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE constants_pkg AS
    -----------------
    -- error codes --
    -----------------

    -- user_pkg errors
    ERR_DUPLICATE_USERNAME CONSTANT NUMBER := -20001;
    ERR_ROLE_NOT_FOUND CONSTANT NUMBER := -20002;
    ERR_USER_NOT_FOUND CONSTANT NUMBER := -20003;
    ERR_CANNOT_DELETE_LAST_ADMIN CONSTANT NUMBER := -20004;
    ERR_TAG_COLOR_INVALID CONSTANT NUMBER := -20005;
    ERR_TAG_NOT_FOUND CONSTANT NUMBER := -20006;
    ERR_DUPLICATE_TAG_ON_SNIPPET CONSTANT NUMBER := -20007;
    ERR_SNIPPET_NOT_FOUND CONSTANT NUMBER := -20008;
    ERR_TAG_NOT_ON_SNIPPET CONSTANT NUMBER :=  -20009;
END constants_pkg;
/