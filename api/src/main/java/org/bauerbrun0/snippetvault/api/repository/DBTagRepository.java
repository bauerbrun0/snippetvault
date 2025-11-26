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
    private final SimpleJdbcCall updateTagCall;
    private final SimpleJdbcCall deleteTagCall;
    private final SimpleJdbcCall getTagCall;
    private final SimpleJdbcCall getTagsCall;

    public DBTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.createTagCall = createCreateTagCall(jdbcTemplate);
        this.updateTagCall = createUpdateTagCall(jdbcTemplate);
        this.deleteTagCall = createDeleteTagCall(jdbcTemplate);
        this.getTagCall = createGetTagCall(jdbcTemplate);
        this.getTagsCall = createGetTagsCall(jdbcTemplate);
    }

    private static SimpleJdbcCall createCreateTagCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("TAG_PKG")
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

    private static SimpleJdbcCall createUpdateTagCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("TAG_PKG")
                .withProcedureName("UPDATE_TAG")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID", "P_NAME", "P_COLOR")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlParameter("P_NAME", Types.VARCHAR),
                        new SqlParameter("P_COLOR", Types.VARCHAR),
                        new SqlOutParameter("P_TAG", Types.REF_CURSOR)
                )
                .returningResultSet("P_TAG", DBTagRepository::mapTagResultRow);
    }

    private static SimpleJdbcCall createDeleteTagCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("TAG_PKG")
                .withProcedureName("DELETE_TAG")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlOutParameter("P_TAG", Types.REF_CURSOR)
                )
                .returningResultSet("P_TAG", DBTagRepository::mapTagResultRow);
    }

    private static SimpleJdbcCall createGetTagCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("TAG_PKG")
                .withFunctionName("GET_TAG")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_ID", Types.NUMERIC)
                )
                .returningResultSet("RESULT", DBTagRepository::mapTagResultRow);
    }

    private static SimpleJdbcCall createGetTagsCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("TAG_PKG")
                .withFunctionName("GET_TAGS")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_USER_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_USER_ID", Types.NUMERIC)
                )
                .returningResultSet("RESULT", DBTagRepository::mapTagResultRow);
    }

    public static Object mapTagResultRow(ResultSet rs, int rowNum) throws SQLException {
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

    @Override
    public Tag updateTag(Long id, String name, String color) throws TagNotFoundException, InvalidTagColorException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_ID", id, Types.NUMERIC)
                    .addValue("P_NAME", name, Types.VARCHAR)
                    .addValue("P_COLOR", color, Types.VARCHAR);
            Map<String, Object> result = this.updateTagCall.execute(params);
            List<Tag> tags = DBRepositoryUtils.getListFromResultObject(result.get("P_TAG"), Tag.class);
            return tags.isEmpty() ? null : tags.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case TAG_NOT_FOUND:
                    throw new TagNotFoundException();
                case INVALID_TAG_COLOR:
                    throw new InvalidTagColorException();
                default:
                    throw new UserRepositoryException("Failed to update tag", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to update tag", e);
        }
    }

    @Override
    public Tag deleteTag(Long id) throws TagNotFoundException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_ID", id, Types.NUMERIC);
            Map<String, Object> result = this.deleteTagCall.execute(params);
            List<Tag> tags = DBRepositoryUtils.getListFromResultObject(result.get("P_TAG"), Tag.class);
            return tags.isEmpty() ? null : tags.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case TAG_NOT_FOUND:
                    throw new TagNotFoundException();
                default:
                    throw new UserRepositoryException("Failed to delete tag", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to delete tag", e);
        }
    }

    @Override
    public Tag getTag(Long id) throws TagNotFoundException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_ID", id, Types.NUMERIC);
            Map<String, Object> result = this.getTagCall.execute(params);
            List<Tag> tags = DBRepositoryUtils.getListFromResultObject(result.get("RESULT"), Tag.class);
            return tags.isEmpty() ? null : tags.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case TAG_NOT_FOUND:
                    throw new TagNotFoundException();
                default:
                    throw new UserRepositoryException("Failed to get tag", e);
            }
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to get tag", e);
        }
    }

    @Override
    public List<Tag> getTags(Long userId) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_USER_ID", userId, Types.NUMERIC);
            Map<String, Object> result = this.getTagsCall.execute(params);
            return DBRepositoryUtils.getListFromResultObject(result.get("RESULT"), Tag.class);
        } catch (Exception e) {
            throw new UserRepositoryException("Failed to get tags", e);
        }
    }
}
