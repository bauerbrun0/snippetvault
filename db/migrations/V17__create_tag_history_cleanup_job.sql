---------------------------------------------------------------------------
-- V17__create_tag_history_cleanup_job.sql                               --
---------------------------------------------------------------------------
-- Creates a scheduled Oracle job that performs periodic cleanup of the  --
-- tag_history table.                                                    --
---------------------------------------------------------------------------

DECLARE
    job_exists EXCEPTION;
    PRAGMA EXCEPTION_INIT(job_exists, -27475); -- ORA-27475: job does not exist
BEGIN
    BEGIN
        DBMS_SCHEDULER.DROP_JOB('JOB_CLEANUP_TAG_HISTORY', force => TRUE);
    EXCEPTION
        WHEN job_exists THEN NULL;
    END;

    DBMS_SCHEDULER.CREATE_JOB(
        job_name        => 'JOB_CLEANUP_TAG_HISTORY',
        job_type        => 'PLSQL_BLOCK',
        job_action      => '
            BEGIN
                DELETE FROM tag_history
                WHERE history_timestamp < ADD_MONTHS(SYSDATE, -3);
                COMMIT;
            EXCEPTION
                WHEN OTHERS THEN
                    error_log_pkg.log_error(
                        p_error_message => SQLERRM,
                        p_error_backtrace => DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,
                        p_context => ''Cleaning up tag_history'',
                        p_value => ''N/A'',
                        p_api => ''JOB_CLEANUP_TAG_HISTORY''
                    );
            END;',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY; BYHOUR=2',
        enabled         => TRUE
    );
END;
/
