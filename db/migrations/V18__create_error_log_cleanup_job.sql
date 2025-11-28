---------------------------------------------------------------------------
-- V18__create_error_log_cleanup_job.sql                                 --
---------------------------------------------------------------------------
-- Creates a scheduled Oracle job that performs periodic cleanup of the  --
-- error_log table.                                                      --
---------------------------------------------------------------------------

DECLARE
    job_exists EXCEPTION;
    PRAGMA EXCEPTION_INIT(job_exists, -27475); -- ORA-27475: job does not exist
BEGIN
    BEGIN
        DBMS_SCHEDULER.DROP_JOB('JOB_CLEANUP_ERROR_LOG', force => TRUE);
    EXCEPTION
        WHEN job_exists THEN NULL;
    END;

    DBMS_SCHEDULER.CREATE_JOB(
        job_name        => 'JOB_CLEANUP_ERROR_LOG',
        job_type        => 'PLSQL_BLOCK',
        job_action      => '
            BEGIN
                DELETE FROM error_log
                WHERE time < ADD_MONTHS(SYSDATE, -3);
                COMMIT;
            EXCEPTION
                WHEN OTHERS THEN
                    error_log_pkg.log_error(
                        p_error_message => SQLERRM,
                        p_error_backtrace => DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,
                        p_context => ''Cleaning up error_log'',
                        p_value => ''N/A'',
                        p_api => ''JOB_CLEANUP_ERROR_LOG''
                    );
            END;',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY; BYHOUR=1',
        enabled         => TRUE
    );
END;
/
