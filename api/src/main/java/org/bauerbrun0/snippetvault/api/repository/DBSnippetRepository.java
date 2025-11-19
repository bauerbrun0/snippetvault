package org.bauerbrun0.snippetvault.api.repository;

import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.exception.UserRepositoryException;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class DBSnippetRepository implements SnippetRepository {
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall createSnippetCall;


    public DBSnippetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.createSnippetCall = createCreateSnippetCall(jdbcTemplate);
    }

    private static SimpleJdbcCall createCreateSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("CREATE_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_USER_ID", "P_TITLE", "P_DESCRIPTION")
                .declareParameters(
                        new SqlParameter("P_USER_ID", Types.NUMERIC),
                        new SqlParameter("P_TITLE", Types.VARCHAR),
                        new SqlParameter("P_DESCRIPTION", Types.VARCHAR),
                        new SqlOutParameter("P_SNIPPET", Types.REF_CURSOR)
                )
                .returningResultSet("P_SNIPPET", DBSnippetRepository::mapSnippetResultRow);
    }

    private static Object mapSnippetResultRow(ResultSet rs, int rowNum) throws SQLException {
        Snippet snippet = new Snippet();
        snippet.setId(rs.getLong("ID"));
        snippet.setUserId(rs.getLong("USER_ID"));
        snippet.setTitle(rs.getString("TITLE"));
        snippet.setDescription(rs.getString("DESCRIPTION"));
        snippet.setCreated(rs.getTimestamp("CREATED").toLocalDateTime());
        snippet.setUpdated(rs.getTimestamp("CREATED").toLocalDateTime());
        return snippet;
    }

    @Override
    public Snippet createSnippet(Long userId, String title, String description) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_USER_ID", userId, Types.NUMERIC)
                    .addValue("P_TITLE", title, Types.VARCHAR)
                    .addValue("P_DESCRIPTION", description, Types.VARCHAR);
            Map<String, Object> result = this.createSnippetCall.execute(params);
            List<Snippet> snippets = DBRepositoryUtils.getListFromResultObject(result.get("P_SNIPPET"), Snippet.class);
            return snippets.isEmpty() ? null : snippets.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case USER_NOT_FOUND:
                    throw new UserNotFoundException();
                default:
                    throw new UserRepositoryException("Failed to create snippet", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to create snippet", e);
        }
    }
}
