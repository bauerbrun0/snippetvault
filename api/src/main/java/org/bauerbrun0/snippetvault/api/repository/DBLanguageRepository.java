package org.bauerbrun0.snippetvault.api.repository;

import org.bauerbrun0.snippetvault.api.exception.LanguageRepositoryException;
import org.bauerbrun0.snippetvault.api.model.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class DBLanguageRepository implements LanguageRepository {
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall getAllLanguagesCall;

    public DBLanguageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.getAllLanguagesCall = createGetAllLanguagesCall(jdbcTemplate);
    }

    private static SimpleJdbcCall createGetAllLanguagesCall(JdbcTemplate jdbcTemplate) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("GET_LANGUAGES")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("RESULT", Types.REF_CURSOR)
                )
                .returningResultSet("RESULT", DBLanguageRepository::mapLanguageResultRow);
    }

    private static Object mapLanguageResultRow(ResultSet rs, int rowNum) throws SQLException {
        Language language = new Language();
        language.setId(rs.getLong("ID"));
        language.setName(rs.getString("NAME"));
        return language;
    }

    @Override
    public List<Language> getAllLanguages() {
        try {
            Map<String, Object> result = this.getAllLanguagesCall.execute();
            return DBRepositoryUtils.getListFromResultObject(
                    result.get("RESULT"), Language.class
            );
        } catch (Exception e) {
            throw new LanguageRepositoryException("Error fetching languages", e);
        }
    }
}
