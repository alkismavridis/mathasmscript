package eu.alkismavridis.mathasmscript.infrastructure.persistence

import eu.alkismavridis.mathasmscript.entities.parser.result.MasVariable
import eu.alkismavridis.mathasmscript.entities.repo.MasScript
import eu.alkismavridis.mathasmscript.entities.repo.ScriptRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp


@Component
class DbScriptRepository(val jdbcTemplate: JdbcTemplate) : ScriptRepository {
    override fun saveScript(script: MasScript, imports: Collection<MasVariable>) {
        this.jdbcTemplate.update{con ->
            val ps = con.prepareStatement(INSERT_STATEMENT)
            this.prepareStatement(script, ps)
            ps
        }

        JdbcUtils.saveBatch(imports, ScriptImportSetManager(script.fileName, script.theoryId), this.jdbcTemplate)
    }

    override fun find(scriptName:String, theoryId: Long) : MasScript? {
        return this.jdbcTemplate.query(
                "SELECT $ALL_COLUMNS FROM $TABLE_NAME WHERE FILE_NAME = ? AND THEORY_ID = ? LIMIT 1",
                arrayOf(scriptName, theoryId),
                ResultSetExtractor { rs -> if(rs.next()) this.map(rs) else null}
        )
    }

    private fun map(rs: ResultSet) : MasScript? {
        if (rs.isClosed) return null

        return MasScript(
                rs.getLong("THEORY_ID"),
                rs.getString("SOURCE"),
                rs.getString("FILE_NAME"),
                rs.getTimestamp("IMPORTED_AT").toInstant()
        )
    }

    private fun prepareStatement(script:MasScript, ps: PreparedStatement) {
        ps.setLong(1, script.theoryId)
        ps.setString(2, script.fileName)
        ps.setString(3, script.contents)
        ps.setTimestamp(4, Timestamp.from(script.importedAt))
    }

    companion object {
        private const val TABLE_NAME = "SCRIPT"
        private const val ALL_COLUMNS = "THEORY_ID, FILE_NAME, SOURCE, IMPORTED_AT"
        private const val INSERT_STATEMENT = "INSERT INTO $TABLE_NAME ($ALL_COLUMNS) VALUES (?, ?, ?, ?)"
    }
}

private class ScriptImportSetManager(val scriptName:String, val theoryId: Long) : SetManager<MasVariable> {
    override fun getInsertStatement() = "INSERT INTO SCRIPT_IMPORTS (THEORY_ID, SCRIPT_NAME, INTERNAL_STATEMENT_ID, EXTERNAL_STATEMENT_ID) VALUES (?, ?, ?, ?)"

    override fun populateParameters(variable: MasVariable, ps: PreparedStatement) {
        val externalPath = if (variable.value.path.isEmpty()) "" else "${variable.value.path} -> ${variable.importUrl}"

        ps.setLong(1, this.theoryId)
        ps.setString(2, this.scriptName)
        JdbcUtils.setIdOrNull(3, variable.value.id, ps)
        ps.setString(4, externalPath) // TODO rename EXTERNAL_STATEMENT_ID ---> EXTERNAL_URL
    }

    override fun setGeneratedValues(entity: MasVariable, keys: ResultSet) {
        // Nothing to be done
    }
}
