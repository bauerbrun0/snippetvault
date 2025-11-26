package org.bauerbrun0.snippetvault.api.repository;

import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class DBSnippetRepository implements SnippetRepository {
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall createSnippetCall;
    private final SimpleJdbcCall addTagToSnippetCall;
    private final SimpleJdbcCall removeTagFromSnippetCall;
    private final SimpleJdbcCall getPaginatedSnippetsCall;
    private final SimpleJdbcCall getSnippetCall;
    private final SimpleJdbcCall deleteSnippetCall;
    private final SimpleJdbcCall updateSnippetCall;
    private final SimpleJdbcCall createFileCall;
    private final SimpleJdbcCall getFilesCall;
    private final SimpleJdbcCall updateFileCall;
    private final SimpleJdbcCall deleteFileCall;
    private final SimpleJdbcCall getTagsOfSnippetCall;

    public DBSnippetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.createSnippetCall = createCreateSnippetCall(jdbcTemplate);
        this.addTagToSnippetCall = createAddTagToSnippetCall(jdbcTemplate);
        this.removeTagFromSnippetCall = createRemoveTagFromSnippetCall(jdbcTemplate);
        this.getPaginatedSnippetsCall = createGetPaginatedSnippetsCall(jdbcTemplate);
        this.getSnippetCall = createGetSnippetCall(jdbcTemplate);
        this.deleteSnippetCall = createDeleteSnippetCall(jdbcTemplate);
        this.updateSnippetCall = createUpdateSnippetCall(jdbcTemplate);
        this.createFileCall = createCreateFileCall(jdbcTemplate);
        this.getFilesCall = createGetFilesCall(jdbcTemplate);
        this.updateFileCall = createUpdateFileCall(jdbcTemplate);
        this.deleteFileCall = createDeleteFileCall(jdbcTemplate);
        this.getTagsOfSnippetCall = createGetTagsOfSnippetCall(jdbcTemplate);
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

    private static SimpleJdbcCall createAddTagToSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("ADD_TAG_TO_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_SNIPPET_ID", "P_TAG_ID")
                .declareParameters(
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC),
                        new SqlParameter("P_TAG_ID", Types.NUMERIC)
                );
    }

    private static SimpleJdbcCall createRemoveTagFromSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("REMOVE_TAG_FROM_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_SNIPPET_ID", "P_TAG_ID")
                .declareParameters(
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC),
                        new SqlParameter("P_TAG_ID", Types.NUMERIC)
                );
    }

    private static SimpleJdbcCall createGetPaginatedSnippetsCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("GET_PAGINATED_SNIPPETS")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames(
                        "P_USER_ID", "P_SEARCH_QUERY", "P_TAG_IDS", "P_LANGUAGE_IDS", "P_PAGE_NUMBER", "P_PAGE_SIZE"
                )
                .declareParameters(
                        new SqlParameter("P_USER_ID", Types.NUMERIC),
                        new SqlParameter("P_SEARCH_QUERY", Types.VARCHAR),
                        new SqlParameter("P_TAG_IDS", Types.ARRAY),
                        new SqlParameter("P_LANGUAGE_IDS", Types.ARRAY),
                        new SqlParameter("P_PAGE_NUMBER", Types.NUMERIC),
                        new SqlParameter("P_PAGE_SIZE", Types.NUMERIC),
                        new SqlOutParameter("P_TOTAL_COUNT", Types.NUMERIC),
                        new SqlOutParameter("P_SNIPPETS", Types.REF_CURSOR)
                )
                .returningResultSet("P_SNIPPETS", DBSnippetRepository::mapDetailedSnippetResultRow);
    }

    private static SimpleJdbcCall createGetSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withFunctionName("GET_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_ID", Types.NUMERIC)
                )
                .returningResultSet("RESULT", DBSnippetRepository::mapSnippetResultRow);
    }

    private static SimpleJdbcCall createDeleteSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("DELETE_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlOutParameter("P_SNIPPET", Types.REF_CURSOR)
                )
                .returningResultSet("P_SNIPPET", DBSnippetRepository::mapSnippetResultRow);
    }

    private static SimpleJdbcCall createUpdateSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("UPDATE_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlParameter("P_TITLE", Types.VARCHAR),
                        new SqlParameter("P_DESCRIPTION", Types.VARCHAR),
                        new SqlOutParameter("P_SNIPPET", Types.REF_CURSOR)
                )
                .returningResultSet("P_SNIPPET", DBSnippetRepository::mapSnippetResultRow);
    }

    private static SimpleJdbcCall createCreateFileCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("CREATE_FILE")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_SNIPPET_ID", "P_FILENAME", "P_CONTENT", "P_LANGUAGE_ID")
                .declareParameters(
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC),
                        new SqlParameter("P_FILENAME", Types.VARCHAR),
                        new SqlParameter("P_CONTENT", Types.CLOB),
                        new SqlParameter("P_LANGUAGE_ID", Types.NUMERIC),
                        new SqlOutParameter("P_FILE", Types.REF_CURSOR)
                )
                .returningResultSet("P_FILE", DBSnippetRepository::mapFileResultRow);
    }

    private static SimpleJdbcCall createGetFilesCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withFunctionName("GET_FILES_OF_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_SNIPPET_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC)
                )
                .returningResultSet("RESULT", DBSnippetRepository::mapFileResultRow);
    }

    private static SimpleJdbcCall createUpdateFileCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("UPDATE_FILE")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID", "P_SNIPPET_ID", "P_FILENAME", "P_CONTENT", "P_LANGUAGE_ID")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC),
                        new SqlParameter("P_FILENAME", Types.VARCHAR),
                        new SqlParameter("P_CONTENT", Types.CLOB),
                        new SqlParameter("P_LANGUAGE_ID", Types.NUMERIC),
                        new SqlOutParameter("P_FILE", Types.REF_CURSOR)
                )
                .returningResultSet("P_FILE", DBSnippetRepository::mapFileResultRow);
    }

    private static SimpleJdbcCall createDeleteFileCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withProcedureName("DELETE_FILE")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_ID", "P_SNIPPET_ID")
                .declareParameters(
                        new SqlParameter("P_ID", Types.NUMERIC),
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC),
                        new SqlOutParameter("P_FILE", Types.REF_CURSOR)
                )
                .returningResultSet("P_FILE", DBSnippetRepository::mapFileResultRow);
    }

    private static SimpleJdbcCall createGetTagsOfSnippetCall(JdbcTemplate template) {
        return new SimpleJdbcCall(template)
                .withCatalogName("SNIPPET_PKG")
                .withFunctionName("GET_TAGS_OF_SNIPPET")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("P_SNIPPET_ID")
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR),
                        new SqlParameter("P_SNIPPET_ID", Types.NUMERIC)
                )
                .returningResultSet("RESULT", DBTagRepository::mapTagResultRow);
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

    private static Object mapDetailedSnippetResultRow(ResultSet rs, int rowNum) throws SQLException {
        DetailedSnippet snippet = new DetailedSnippet();
        snippet.setId(rs.getLong("ID"));
        snippet.setUserId(rs.getLong("USER_ID"));
        snippet.setTitle(rs.getString("TITLE"));
        snippet.setDescription(rs.getString("DESCRIPTION"));
        snippet.setCreated(rs.getTimestamp("CREATED").toLocalDateTime());
        snippet.setUpdated(rs.getTimestamp("CREATED").toLocalDateTime());
        snippet.setRelevance(rs.getLong("RELEVANCE"));
        snippet.setFileCount(rs.getLong("FILE_COUNT"));

        Array langArray = rs.getArray("LANGUAGE_IDS");
        Array tagArray  = rs.getArray("TAG_IDS");

        List<Long> languageIds = null;
        List<Long> tagIds = null;

        if (langArray != null) {
            Object[] arr = (Object[]) langArray.getArray();
            languageIds = Arrays.stream(arr)
                    .map(v -> v == null ? null : ((Number) v).longValue())
                    .toList();
        }
        snippet.setLanguageIds(languageIds);


        if (tagArray != null) {
            Object[] arr = (Object[]) tagArray.getArray();
            tagIds = Arrays.stream(arr)
                    .map(v -> v == null ? null : ((Number) v).longValue())
                    .toList();
        }
        snippet.setTagIds(tagIds);

        return snippet;
    }

    private static Object mapFileResultRow(ResultSet rs, int rowNum) throws SQLException {
        File file = new File();
        file.setId(rs.getLong("ID"));
        file.setSnippetId(rs.getLong("SNIPPET_ID"));
        file.setFilename(rs.getString("FILENAME"));
        file.setContent(rs.getString("CONTENT"));
        file.setLanguageId(rs.getLong("LANGUAGE_ID"));
        file.setCreated(rs.getTimestamp("CREATED").toLocalDateTime());
        file.setUpdated(rs.getTimestamp("UPDATED").toLocalDateTime());
        return file;
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
                    throw new SnippetRepositoryException("Failed to create snippet", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to create snippet", e);
        }
    }

    @Override
    public void addTagToSnippet(Long snippetId, Long tagId) throws TagNotFoundException, SnippetNotFoundException, DuplicateTagOnSnippetException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC)
                    .addValue("P_TAG_ID", tagId, Types.NUMERIC);
            this.addTagToSnippetCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case TAG_NOT_FOUND:
                    throw new TagNotFoundException();
                case SNIPPET_NOT_FOUND:
                    throw new SnippetNotFoundException();
                case DUPLICATE_TAG_ON_SNIPPET:
                    throw new DuplicateTagOnSnippetException();
                default:
                    throw new SnippetRepositoryException("Failed to add tag to snippet", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to add tag to snippet", e);
        }
    }

    @Override
    public void removeTagFromSnippet(Long snippetId, Long tagId) throws TagNotOnSnippetException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC)
                    .addValue("P_TAG_ID", tagId, Types.NUMERIC);
            this.removeTagFromSnippetCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case TAG_NOT_ON_SNIPPET:
                    throw new TagNotOnSnippetException();
                default:
                    throw new SnippetRepositoryException("Failed to remove tag from snippet", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to remove tag from snippet", e);
        }
    }

    @Override
    public List<Tag> getTagsOfSnippet(Long snippetId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC);
        Map<String, Object> result;
        try {
            result = this.getTagsOfSnippetCall.execute(params);
            return DBRepositoryUtils.getListFromResultObject(result.get("RESULT"), Tag.class);
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to retrieve tags of snippet", e);
        }
    }

    @Override
    public SnippetSearchResult getPaginatedSnippets(
            Long userId, String searchQuery, List<Long> tagIds, List<Long> languageIds, Long pageNumber, Long pageSize
    ) {
        Map<String, Object> result;
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("P_USER_ID", userId)
                    .addValue("P_SEARCH_QUERY", searchQuery)
                    .addValue(
                            "P_TAG_IDS",
                            tagIds == null
                                    ? null
                                    : DBRepositoryUtils.toOracleArray(this.jdbcTemplate,"NUMBER_ARRAY", tagIds.toArray()),
                            Types.ARRAY,
                            "NUMBER_ARRAY"
                    )
                    .addValue(
                            "P_LANGUAGE_IDS",
                            languageIds == null
                                    ? null
                                    : DBRepositoryUtils.toOracleArray(this.jdbcTemplate,"NUMBER_ARRAY", languageIds.toArray()),
                            Types.ARRAY,
                            "NUMBER_ARRAY"
                    )
                    .addValue("P_PAGE_NUMBER", pageNumber)
                    .addValue("P_PAGE_SIZE", pageSize);
            result = this.getPaginatedSnippetsCall.execute(params);
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to retrieve snippets", e);
        }

        List<DetailedSnippet> snippets = DBRepositoryUtils.getListFromResultObject(
                result.get("P_SNIPPETS"), DetailedSnippet.class
        );
        Number totalCountNum = (Number) result.get("P_TOTAL_COUNT");
        Long totalCount = totalCountNum == null ? 0L : totalCountNum.longValue();
        return new SnippetSearchResult(snippets, totalCount);
    }

    @Override
    public Snippet getSnippet(Long snippetId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", snippetId, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.getSnippetCall.execute(params);
        } catch (Exception e) {
            if (e instanceof DataAccessException dae &&
                    DBRepositoryUtils.getSqlErrorCode(dae) == DBErrorCodes.SNIPPET_NOT_FOUND.getCode()) {
                throw new SnippetNotFoundException();
            }
            throw new SnippetRepositoryException("Failed to retrieve snippet by id", e);
        }

        List<Snippet> snippets = DBRepositoryUtils.getListFromResultObject(result.get("RESULT"), Snippet.class);
        return snippets.isEmpty() ? null : snippets.get(0);
    }

    @Override
    public Snippet deleteSnippet(Long snippetId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", snippetId, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.deleteSnippetCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case SNIPPET_NOT_FOUND:
                    throw new SnippetNotFoundException();
                default:
                    throw new SnippetRepositoryException("Failed to delete snippet", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to delete snippet", e);
        }

        List<Snippet> snippets = DBRepositoryUtils.getListFromResultObject(result.get("P_SNIPPET"), Snippet.class);
        return snippets.isEmpty() ? null : snippets.get(0);
    }

    @Override
    public Snippet updateSnippet(Long snippetId, String title, String description) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", snippetId, Types.NUMERIC)
                .addValue("P_TITLE", title, Types.VARCHAR)
                .addValue("P_DESCRIPTION", description, Types.VARCHAR);

        Map<String, Object> result;
        try {
            result = this.updateSnippetCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case SNIPPET_NOT_FOUND:
                    throw new SnippetNotFoundException();
                default:
                    throw new SnippetRepositoryException("Failed to update snippet", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to update snippet", e);
        }

        List<Snippet> snippets = DBRepositoryUtils.getListFromResultObject(result.get("P_SNIPPET"), Snippet.class);
        return snippets.isEmpty() ? null : snippets.get(0);
    }

    @Override
    public File createFile(Long snippetId, String filename, String content, Long languageId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC)
                .addValue("P_FILENAME", filename, Types.VARCHAR)
                .addValue("P_CONTENT", content, Types.CLOB)
                .addValue("P_LANGUAGE_ID", languageId, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.createFileCall.execute(params);
            List<File> files = DBRepositoryUtils.getListFromResultObject(result.get("P_FILE"), File.class);
            return files.isEmpty() ? null : files.get(0);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case SNIPPET_NOT_FOUND:
                    throw new SnippetNotFoundException();
                case LANGUAGE_NOT_FOUND:
                    throw new LanguageNotFoundException();
                default:
                    throw new SnippetRepositoryException("Failed to create file", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to create file", e);
        }
    }

    @Override
    public List<File> getFiles(Long snippetId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.getFilesCall.execute(params);
            return DBRepositoryUtils.getListFromResultObject(result.get("RESULT"), File.class);
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to retrieve files for snippet", e);
        }
    }

    @Override
    public File updateFile(Long fileId, Long snippetId, String filename, String content, Long languageId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", fileId, Types.NUMERIC)
                .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC)
                .addValue("P_FILENAME", filename, Types.VARCHAR)
                .addValue("P_CONTENT", content, Types.CLOB)
                .addValue("P_LANGUAGE_ID", languageId, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.updateFileCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case FILE_NOT_FOUND:
                    throw new FileNotFoundException();
                case LANGUAGE_NOT_FOUND:
                    throw new LanguageNotFoundException();
                default:
                    throw new SnippetRepositoryException("Failed to update file", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to update file", e);
        }

        List<File> files = DBRepositoryUtils.getListFromResultObject(result.get("P_FILE"), File.class);
        return files.isEmpty() ? null : files.get(0);
    }

    @Override
    public File deleteFile(Long fileId, Long snippetId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ID", fileId, Types.NUMERIC)
                .addValue("P_SNIPPET_ID", snippetId, Types.NUMERIC);

        Map<String, Object> result;
        try {
            result = this.deleteFileCall.execute(params);
        } catch (DataAccessException e) {
            switch (DBErrorCodes.fromCode(DBRepositoryUtils.getSqlErrorCode(e))) {
                case FILE_NOT_FOUND:
                    throw new FileNotFoundException();
                default:
                    throw new SnippetRepositoryException("Failed to delete file", e);
            }
        } catch (Exception e) {
            throw new SnippetRepositoryException("Failed to delete file", e);
        }

        List<File> files = DBRepositoryUtils.getListFromResultObject(result.get("P_FILE"), File.class);
        return files.isEmpty() ? null : files.get(0);
    }
}
