package org.bauerbrun0.snippetvault.api.repository;

import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.model.Tag;
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

@Repository
public class DBTagRepository implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall createTagCall;

    public DBTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.createTagCall = createCreateTagCall(jdbcTemplate);
    }

    private static SimpleJdbcCall createCreateTagCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("CREATE_TAG")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_USER_ID", "P_NAME", "P_COLOR")
                .declareParameters(
                        new SqlParameter("P_USER_ID", Types.NUMERIC),
                        new SqlParameter("P_NAME", Types.VARCHAR),
                        new SqlParameter("P_COLOR", Types.VARCHAR),
                        new SqlOutParameter("P_TAG", Types.REF_CURSOR)
                )
                .returningResultSet("P_TAG", DBTagRepository::mapTagResultRow);
    }

    private static Object mapTagResultRow(ResultSet rs, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong("ID"));
        tag.setUserId(rs.getLong("USER_ID"));
        tag.setName(rs.getString("NAME"));
        tag.setColor(rs.getString("COLOR"));
        tag.setCreated(rs.getTimestamp("CREATED").toLocalDateTime());
        return tag;
    }

    @Override
    public Tag createTag(Long userId, String name, String color) throws UserNotFoundException, InvalidTagColorException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_USER_ID", userId, Types.NUMERIC)
                    .addValue("P_NAME", name, Types.VARCHAR)
                    .addValue("P_COLOR", color, Types.VARCHAR);
            Map<String, Object> result = this.createTagCall.execute(params);
            List<Tag> tags = DBRepositoryUtils.getListFromResultObject(result.get("P_TAG"), Tag.class);
            return tags.isEmpty() ? null : tags.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case USER_NOT_FOUND:
                    throw new UserNotFoundException();
                case INVALID_TAG_COLOR:
                    throw new InvalidTagColorException();
                default:
                    throw new UserRepositoryException("Failed to create tag", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to create tag", e);
        }
    }

}
