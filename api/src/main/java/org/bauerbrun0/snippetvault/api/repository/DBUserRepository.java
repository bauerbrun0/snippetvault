package org.bauerbrun0.snippetvault.api.repository;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleConnection;
import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Repository
public class DBUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall getUserByUsernameCall;
    private final SimpleJdbcCall getUserByIdCall;
    private final SimpleJdbcCall getUsersCall;
    private final SimpleJdbcCall deleteUserCall;
    private final SimpleJdbcCall createUserCall;
    private final SimpleJdbcCall updateUserCall;
    private final SimpleJdbcCall getUserRolesCall;

    public DBUserRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
        this.getUserByUsernameCall = createGetUserByUsernameCall(template);
        this.getUserByIdCall = createGetUserByIdCall(template);
        this.getUsersCall = createGetUsersCall(template);
        this.deleteUserCall = createDeleteUserCall(template);
        this.createUserCall = createCreateUserCall(template);
        this.updateUserCall = createUpdateUserCall(template);
        this.getUserRolesCall = createGetUserRolesCall(template);
    }

    private static SimpleJdbcCall createGetUserByUsernameCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withFunctionName("GET_USER_BY_USERNAME")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_USERNAME")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_USERNAME", Types.VARCHAR)
                )
                .returningResultSet("RESULT", DBUserRepository::mapUserResultRow);
    }

    private static SimpleJdbcCall createGetUserByIdCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withFunctionName("GET_USER")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_ID", Types.VARCHAR)
                )
                .returningResultSet("RESULT", DBUserRepository::mapUserResultRow);
    }

    private static SimpleJdbcCall createGetUsersCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withFunctionName("GET_USERS")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR)
                )
                .returningResultSet("RESULT", DBUserRepository::mapUserResultRow);
    }

    private static SimpleJdbcCall createDeleteUserCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withProcedureName("DELETE_USER")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlInOutParameter("P_ID", Types.NUMERIC),
                        new SqlOutParameter("P_USER", Types.REF_CURSOR)
                )
                .returningResultSet("P_USER", DBUserRepository::mapUserResultRow);
    }

    private static SimpleJdbcCall createCreateUserCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withProcedureName("CREATE_USER")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_USERNAME", "P_PASSWORD_HASH", "P_ROLES")
                .declareParameters(
                        new SqlParameter("P_USERNAME", Types.VARCHAR),
                        new SqlParameter("P_PASSWORD_HASH", Types.VARCHAR),
                        new SqlParameter("P_ROLES", Types.ARRAY),
                        new SqlOutParameter("P_USER", Types.REF_CURSOR)
                )
                .returningResultSet("P_USER", DBUserRepository::mapUserResultRow);
    }

    private static SimpleJdbcCall createUpdateUserCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withProcedureName("UPDATE_USER")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID", "P_USERNAME", "P_PASSWORD_HASH")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlParameter("P_USERNAME", Types.VARCHAR),
                        new SqlParameter("P_PASSWORD_HASH", Types.VARCHAR),
                        new SqlOutParameter("P_USER", Types.REF_CURSOR)
                )
                .returningResultSet("P_USER", DBUserRepository::mapUserResultRow);
    }

    private static SimpleJdbcCall createGetUserRolesCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("USER_PKG")
                .withFunctionName("GET_USER_ROLES")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_ID", Types.NUMERIC)
                )
                .returningResultSet("RESULT", DBUserRepository::mapRoleResultRow);
    }

    private static Object mapUserResultRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("ID"));
        user.setUsername(rs.getString("USERNAME"));
        user.setPasswordHash(rs.getString("PASSWORD_HASH"));
        user.setCreated(rs.getTimestamp("CREATED").toLocalDateTime());
        return user;
    }

    private static Object mapRoleResultRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("ID"));
        role.setName(rs.getString("NAME"));
        return role;
    }

    @Override
    public User getUserByUsername(String username) throws UserNotFoundException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_USERNAME", username, Types.VARCHAR);

        Map<String, Object> result;
        try {
            result = this.getUserByUsernameCall.execute(params);
        } catch (Exception e) {
            if (e instanceof DataAccessException dae &&
                    this.getSqlErrorCode(dae) == DBErrorCodes.USER_NOT_FOUND.getCode()) {
                throw new UserNotFoundException();
            }
            throw new UserRepositoryException("Failed to retrieve user by username", e);
        }

        List<User> users = this.getListFromResultObject(result.get("RESULT"), User.class);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User getUser(Long id) throws UserNotFoundException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", id, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.getUserByIdCall.execute(params);
        } catch (Exception e) {
            if (e instanceof DataAccessException dae &&
                    this.getSqlErrorCode(dae) == DBErrorCodes.USER_NOT_FOUND.getCode()) {
                throw new UserNotFoundException();
            }
            throw new UserRepositoryException("Failed to retrieve user by id", e);
        }

        List<User> users = this.getListFromResultObject(result.get("RESULT"), User.class);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<User> getUsers() {
        Map<String, Object> result;
        try {
            result = this.getUsersCall.execute();
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to retrieve users", e);
        }

        return this.getListFromResultObject(result.get("RESULT"), User.class);
    }

    @Override
    public User deleteUser(Long id) throws UserNotFoundException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", id, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.deleteUserCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(getSqlErrorCode(e))) {
                case USER_NOT_FOUND:
                    throw new UserNotFoundException();
                case CANNOT_DELETE_LAST_ADMIN:
                    throw new CannotDeleteLastAdminException();
                default:
                    throw new UserRepositoryException("Failed to delete user", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to delete user", e);
        }

        List<User> users = this.getListFromResultObject(result.get("P_USER"), User.class);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User createUser(String username, String passwordHash, String[] roles) throws DuplicateUsernameException, RoleNotFoundException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_USERNAME", username, Types.VARCHAR)
                    .addValue("P_PASSWORD_HASH", passwordHash, Types.VARCHAR)
                    .addValue("P_ROLES", this.toOracleArray("ROLE_ARRAY", roles));
            Map<String, Object> result = this.createUserCall.execute(params);
            List<User> users = this.getListFromResultObject(result.get("P_USER"), User.class);
            return users.isEmpty() ? null : users.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(getSqlErrorCode(e))) {
                case DUPLICATE_USERNAME:
                    throw new DuplicateUsernameException();
                case ROLE_NOT_FOUND:
                    throw new RoleNotFoundException();
                default:
                    throw new UserRepositoryException("Failed to create user", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to create user", e);
        }
    }

    @Override
    public User updateUser(Long id, String username, String passwordHash) throws UserNotFoundException, DuplicateUsernameException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", id, Types.NUMERIC)
                .addValue("P_USERNAME", username, Types.VARCHAR)
                .addValue("P_PASSWORD_HASH", passwordHash, Types.VARCHAR);

        Map<String, Object> result;
        try {
            result = this.updateUserCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(getSqlErrorCode(e))) {
                case USER_NOT_FOUND:
                    throw new UserNotFoundException();
                case DUPLICATE_USERNAME:
                    throw new DuplicateUsernameException();
                default:
                    throw new UserRepositoryException("Failed to update user", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to update user", e);
        }

        List<User> users = this.getListFromResultObject(result.get("P_USER"), User.class);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<Role> getUserRoles(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", id, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.getUserRolesCall.execute(params);
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to get user roles", e);
        }

        return this.getListFromResultObject(result.get("RESULT"), Role.class);
    }

    private int getSqlErrorCode(DataAccessException e) {
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

    private <T> List<T> getListFromResultObject(Object obj, Class<T> type) {
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

    private Array toOracleArray(String typeName, Object[] values) throws SQLException {
        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
            return oracleConn.createOracleArray(typeName, values);
        }
    }
}
