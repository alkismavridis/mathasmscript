package eu.alkismavridis.mathasmscript.infrastructure.persistence

import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant


@Component
class DbStatementRepository(val jdbcTemplate: JdbcTemplate, val namedJdbcTemplate: NamedParameterJdbcTemplate) : StatementRepository {
    override fun findByPath(path: String): FixedMasStatement? {
        val statementsToUpdate = this.findAll(listOf(path))
        return if (statementsToUpdate.isEmpty())
            null else
            statementsToUpdate[0]
    }

    override fun findExistingNames(fullNames:Collection<String>) : List<String> {
        val params: MutableMap<String, Any> = HashMap()
        params["fullNames"] = fullNames

        return this.namedJdbcTemplate.query(
                "SELECT PATH FROM STATEMENT WHERE PATH IN (:fullNames) LIMIT ${fullNames.size}",
                params
        ) { rs, _ -> rs.getString("PATH") }
    }

    override fun findAll(fullNames:Collection<String>) : List<FixedMasStatement> {
        val params: MutableMap<String, Any> = HashMap()
        params["fullNames"] = fullNames

        return this.namedJdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM STATEMENT WHERE PATH IN (:fullNames) LIMIT ${fullNames.size}",
                params
        ) { rs, _ -> this.map(rs) }
    }

    fun findAllDependingOn(statementId: Long) : List<FixedMasStatement> {
        return this.jdbcTemplate.query(
                "SELECT * FROM STATEMENT WHERE SCRIPT IN (" +
                        "SELECT SCRIPT_NAME FROM SCRIPT_IMPORTS  WHERE INTERNAL_STATEMENT_ID = ?" +
                    ")",
                arrayOf(statementId)
        ) { rs, _ -> this.map(rs) }
    }

    override fun findAllByPackage(packageName: String): List<FixedMasStatement> {
        return this.jdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM STATEMENT WHERE PACKAGE_ID = (" +
                        "SELECT ID FROM PACKAGE WHERE PATH = ? LIMIT 1" +
                     ")",
                arrayOf(packageName)
        ) { rs, _ -> this.map(rs) }
    }


    /// INSERTS
    override fun saveAll(statements:List<FixedMasStatement>, scriptName: String, creationDate: Instant) {
        JdbcUtils.saveBatch(statements, StatementSetManager(scriptName, creationDate), this.jdbcTemplate)
    }

    override fun update(statement:FixedMasStatement, previousName: String) {
        log.info("Moving statement {} to {}", previousName, statement.path)

        this.jdbcTemplate.update(
                "UPDATE STATEMENT SET PATH = ?, PACKAGE_ID = ? WHERE ID = ? LIMIT 1",
                statement.path,
                statement.packageId,
                statement.id
        )

        this.jdbcTemplate.update(
                "INSERT INTO MAS_CHANGE_LOG (CHANGE_TYPE, INIT_STATE, FINAL_STATE) VALUES (?, ?, ?)",
                ChangeType.STATEMENT_MOVE.name,
                previousName,
                statement.path
        )
    }

    private fun map(rs: ResultSet) : FixedMasStatement? {
        if (rs.isClosed) return null

        return FixedMasStatement(
                rs.getString("PATH"),
                rs.getLong("PACKAGE_ID"),
                StatementType.valueOf(rs.getString("TYPE")),
                rs.getString("TEXT"),
                rs.getLong("ID")
        )
    }

    companion object {
        private const val COLUMNS_FOR_FETCHING = "ID, PATH, PACKAGE_ID, TYPE, TEXT"
        private val log = LoggerFactory.getLogger(DbStatementRepository::class.java)
    }
}


private class StatementSetManager(val scriptName: String, val creationDate: Instant) : SetManager<FixedMasStatement> {
    override fun getInsertStatement() = "INSERT INTO STATEMENT (PATH, PACKAGE_ID, SCRIPT, TYPE, TEXT, CREATED_AT) VALUES (?, ?, ?, ?, ?, ?)"

    override fun populateParameters(value: FixedMasStatement, ps:PreparedStatement) {
        ps.setString(1, value.path)
        ps.setLong(2, value.packageId)
        ps.setString(3, this.scriptName)
        ps.setString(4, value.type.name)
        ps.setString(5, value.text)
        ps.setTimestamp(6, Timestamp.from(this.creationDate))
    }

    override fun setGeneratedValues(entity: FixedMasStatement, keys: ResultSet) {
        // entity provides no id at the moment
    }
}
