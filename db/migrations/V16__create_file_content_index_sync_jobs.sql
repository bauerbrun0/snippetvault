---------------------------------------------------------------------------
-- V16__create_file_content_index_sync_jobs.sql                          --
---------------------------------------------------------------------------
-- Creates two scheduled jobs:                                           --
--                                                                       --
-- 1) JOB_SYNC_IDX_FILE_CONTENT                                          --
--    - Runs every minute                                                --
--    - Calls CTX_DDL.SYNC_INDEX to apply pending Oracle Text updates    --
--    - Ensures that new snippet file content is indexed quickly         --
--                                                                       --
-- 2) JOB_OPTIMIZE_IDX_FILE_CONTENT                                      --
--    - Runs daily at 03:00                                              --
--    - Optimizes the Oracle Text index for performance improvements     --
---------------------------------------------------------------------------

DECLARE
    job_exists EXCEPTION;
    PRAGMA EXCEPTION_INIT(job_exists, -27475); -- ORA-27475: job does not exist
BEGIN
    BEGIN
        DBMS_SCHEDULER.DROP_JOB('JOB_SYNC_IDX_FILE_CONTENT', force => TRUE);
    EXCEPTION
        WHEN job_exists THEN NULL;
    END;

    BEGIN
        DBMS_SCHEDULER.DROP_JOB('JOB_OPTIMIZE_IDX_FILE_CONTENT', force => TRUE);
    EXCEPTION
        WHEN job_exists THEN NULL;
    END;

    DBMS_SCHEDULER.CREATE_JOB(
            job_name        => 'JOB_SYNC_IDX_FILE_CONTENT',
            job_type        => 'PLSQL_BLOCK',
            job_action      => 'BEGIN CTX_DDL.SYNC_INDEX(''IDX_FILE_CONTENT''); END;',
            start_date      => SYSTIMESTAMP,
            repeat_interval => 'FREQ=MINUTELY; INTERVAL=1',
            enabled         => TRUE
    );

    DBMS_SCHEDULER.CREATE_JOB(
            job_name        => 'JOB_OPTIMIZE_IDX_FILE_CONTENT',
            job_type        => 'PLSQL_BLOCK',
            job_action      => 'BEGIN CTX_DDL.OPTIMIZE_INDEX(''IDX_FILE_CONTENT'', ''FULL''); END;',
            start_date      => SYSTIMESTAMP,
            repeat_interval => 'FREQ=DAILY; BYHOUR=3; BYMINUTE=0; BYSECOND=0',
            enabled         => TRUE
    );
END;
/
