package eu.alkismavridis.mathasmscript.infrastructure.persistence

import eu.alkismavridis.mathasmscript.model.repo.MasScript
import eu.alkismavridis.mathasmscript.model.repo.ScriptRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.ImportId
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant

private class ScriptInsertBatch {
    companion object {
        fun prepareStatement(script:MasScript, ps: PreparedStatement) {
            ps.setString(1, script.fileName)
            ps.setString(2, script.contents)
            ps.setTimestamp(3, Timestamp.from(script.importedAt))
        }
    }
}

private class ScriptImportSetManager(val scriptName:String) : SetManager<ImportId> {
    override fun getInsertStatement() = "INSERT INTO SCRIPT_IMPORTS (SCRIPT_NAME, INTERNAL_STATEMENT_ID, EXTERNAL_STATEMENT_ID) VALUES (?, ?, ?)"

    override fun populateParameters(value: ImportId, ps: PreparedStatement) {
        ps.setString(1, this.scriptName)
        JdbcUtils.setIdOrNull(2, value.internalId, ps)
        ps.setString(3, value.externalId)
    }

    override fun setGeneratedValues(entity: ImportId, keys: ResultSet) {
        // nothing to be done
    }

}


@Component
class DbScriptRepository(val jdbcTemplate: JdbcTemplate, val namedJdbcTemplate: NamedParameterJdbcTemplate) : ScriptRepository {
    companion object {
        private const val TABLE_NAME = "SCRIPT"
        private const val ALL_COLUMNS = "FILE_NAME, SOURCE, IMPORTED_AT"
        private const val INSERT_STATEMENT = "INSERT INTO $TABLE_NAME ($ALL_COLUMNS) VALUES (?, ?, ?)"
    }

    /// MAPPERS
    private fun map(rs: ResultSet) : MasScript? {
        if (rs.isClosed) return null

        return MasScript(
                rs.getString("SOURCE"),
                rs.getString("FILE_NAME"),
                rs.getTimestamp("IMPORTED_AT").toInstant()
        )
    }

    override fun saveScript(fileName: String, packageName: String, source: String, importIds:List<ImportId>) {
        val dbModel = MasScript(source, fileName, Instant.now())
        this.jdbcTemplate.update{con ->
            val ps = con.prepareStatement(INSERT_STATEMENT)
            ScriptInsertBatch.prepareStatement(dbModel, ps)
            ps
        }

        JdbcUtils.saveBatch(importIds, ScriptImportSetManager(fileName), this.jdbcTemplate)
    }

    fun find(scriptName:String) : MasScript? {
        return this.jdbcTemplate.query(
                "SELECT $ALL_COLUMNS FROM $TABLE_NAME WHERE FILE_NAME = ? LIMIT 1",
                arrayOf(scriptName),
                ResultSetExtractor { rs -> if(rs.next()) this.map(rs) else null}
        )
    }
}
