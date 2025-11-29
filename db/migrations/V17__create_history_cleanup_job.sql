---------------------------------------------------------------------------
-- V17__create_history_cleanup_job.sql                                   --
---------------------------------------------------------------------------
-- Creates a scheduled Oracle job that performs periodic cleanup of      --
-- history tables.                                                       --
---------------------------------------------------------------------------

DECLARE
    job_exists EXCEPTION;
    PRAGMA EXCEPTION_INIT(job_exists, -27475); -- ORA-27475: job does not exist
BEGIN
    BEGIN
        DBMS_SCHEDULER.DROP_JOB('JOB_CLEANUP_HISTORY', force => TRUE);
    EXCEPTION
        WHEN job_exists THEN NULL;
    END;

    DBMS_SCHEDULER.CREATE_JOB(
        job_name        => 'JOB_CLEANUP_HISTORY',
        job_type        => 'PLSQL_BLOCK',
        job_action      => '
            BEGIN
                DELETE FROM snippetvault_user_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM role_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM user_role_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM snippet_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM snippetvault_file_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM language_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM tag_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

                DELETE FROM snippet_tag_history
                WHERE history_timestamp < ADD_MONTHS(SYSTIMESTAMP, -1);

            EXCEPTION
                WHEN OTHERS THEN
                    error_log_pkg.log_error(
                        p_error_message => SQLERRM,
                        p_error_backtrace => DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,
                        p_context => ''Cleaning up history'',
                        p_value => ''N/A'',
                        p_api => ''JOB_CLEANUP_HISTORY''
                    );
                RAISE;
            END;',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY; BYHOUR=2',
        enabled         => TRUE
    );
END;
/
