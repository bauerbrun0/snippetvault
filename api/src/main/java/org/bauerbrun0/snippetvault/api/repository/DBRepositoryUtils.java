package org.bauerbrun0.snippetvault.api.repository;

import oracle.jdbc.OracleConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBRepositoryUtils {
    public static <T> List<T> getListFromResultObject(Object obj, Class<T> type) {
        if  (obj == null) {
            throw new IllegalStateException(
                    "Expected a List<" + type.getSimpleName() + "> but got null"
            );
        }

        if (obj instanceof List<?> list) {
            if (list.isEmpty()) {
                return Collections.emptyList();
            }

            List<T> result = new ArrayList<T>();

            for (Object item : list) {
                if (type.isInstance(item)) {
                    result.add(type.cast(item));
                } else {
                    throw new IllegalStateException(
                            "Expected" + type.getSimpleName() + " but got " +
                                    (item == null ? "null" : item.getClass().getSimpleName())
                    );
                }
            }

            return result;
        }

        throw new IllegalStateException(
                "Expected a List<" + type.getSimpleName() + "> but got " +
                        obj.getClass().getSimpleName()
        );
    }

    public static int getSqlErrorCode(DataAccessException e) {
        if (e.getCause() instanceof SQLException sqlException) {
            String message = sqlException.getMessage();
            if (message != null) {
                Pattern pattern = Pattern.compile("ORA-(-?\\d+)");
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
        }
        return 0;
    }

    public static Array toOracleArray(JdbcTemplate jdbcTemplate, String typeName, Object[] values) throws SQLException {
        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
            return oracleConn.createOracleArray(typeName, values);
        }
    }
}
