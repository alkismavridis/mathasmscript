package eu.alkismavridis.mathasmscript.persistence

import eu.alkismavridis.mathasmscript.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.repo.FixedStatementType
import eu.alkismavridis.mathasmscript.repo.StatementRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant


@Component
class DbStatementRepository(val jdbcTemplate: JdbcTemplate, val namedJdbcTemplate: NamedParameterJdbcTemplate) : StatementRepository {
    override fun findByPath(path: String, theoryId: Long): FixedMasStatement? {
        val statementsToUpdate = this.findAll(listOf(path), theoryId)
        return if (statementsToUpdate.isEmpty())
            null else
            statementsToUpdate[0]
    }

    override fun findExistingNames(fullNames: Collection<String>, theoryId: Long): List<String> {
        val params: MutableMap<String, Any> = HashMap()
        params["fullNames"] = fullNames
        params["theoryId"] = theoryId

        return this.namedJdbcTemplate.query(
                "SELECT PATH FROM STATEMENT WHERE THEORY_ID = :theoryId AND PATH IN (:fullNames) LIMIT ${fullNames.size}",
                params
        ) { rs, _ -> rs.getString("PATH") }
    }

    override fun findAll(fullNames: Collection<String>, theoryId: Long): List<FixedMasStatement> {
        val params: MutableMap<String, Any> = HashMap()
        params["fullNames"] = fullNames
        params["theoryId"] = theoryId

        return this.namedJdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM STATEMENT WHERE THEORY_ID = :theoryId AND PATH IN (:fullNames) LIMIT ${fullNames.size}",
                params
        ) { rs, _ -> this.map(rs) }
    }

    override fun findDependenciesOf(statementId: Long): List<FixedMasStatement> {
        return this.jdbcTemplate.query(
                "SELECT * FROM STATEMENT WHERE SCRIPT IN (" +
                        "SELECT SCRIPT_NAME FROM SCRIPT_IMPORTS  WHERE INTERNAL_STATEMENT_ID = ?" +
                        ")",
                arrayOf(statementId)
        ) { rs, _ -> this.map(rs) }
    }

    override fun hasDependencies(statementId: Long): Boolean {
        return this.jdbcTemplate.query(
                "SELECT ID FROM STATEMENT WHERE SCRIPT IN (" +
                        "SELECT SCRIPT_NAME FROM SCRIPT_IMPORTS  WHERE INTERNAL_STATEMENT_ID = ?" +
                        ") LIMIT 1",
                arrayOf(statementId),
                ResultSetExtractor { rs -> rs.next() }
        ) ?: false
    }

    override fun findAllByPackage(packageName: String, theoryId: Long): List<FixedMasStatement> {
        return this.jdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM STATEMENT WHERE PACKAGE_ID = (" +
                        "SELECT ID FROM PACKAGE WHERE THEORY_ID = ? AND PATH = ? LIMIT 1" +
                        ")",
                arrayOf(theoryId, packageName)
        ) { rs, _ -> this.map(rs) }
    }


    /// INSERTS
    override fun saveAll(statements: List<FixedMasStatement>, scriptName: String, creationDate: Instant) {
        JdbcUtils.saveBatch(statements, StatementSetManager(scriptName, creationDate), this.jdbcTemplate)
    }

    override fun update(statement: FixedMasStatement, previousName: String) {
        log.info("Moving statement {} to {}", previousName, statement.path)

        this.jdbcTemplate.update(
                "UPDATE STATEMENT SET THEORY_ID = ? PATH = ?, PACKAGE_ID = ? WHERE ID = ? LIMIT 1",
                statement.theoryId,
                statement.path,
                statement.packageId,
                statement.id
        )

        this.jdbcTemplate.update(
                "INSERT INTO MAS_CHANGE_LOG (THEORY_ID, CHANGE_TYPE, INIT_STATE, FINAL_STATE) VALUES (?, ?, ?)",
                statement.theoryId,
                ChangeType.STATEMENT_MOVE.name,
                previousName,
                statement.path
        )
    }

    override fun existsByParent(path: String, theoryId: Long): Boolean {
        val pattern = if (path.isEmpty()) "%" else "$path.%"

        return this.jdbcTemplate.query(
                "SELECT ID FROM STATEMENT WHERE THEORY_ID = ? AND PATH LIKE ? LIMIT 1",
                arrayOf(theoryId, pattern),
                ResultSetExtractor { rs -> rs.next() }
        ) ?: false
    }

    private fun map(rs: ResultSet): FixedMasStatement? {
        if (rs.isClosed) return null

        return FixedMasStatement(
                rs.getString("PATH"),
                rs.getLong("PACKAGE_ID"),
                FixedStatementType.valueOf(rs.getString("TYPE")),
                rs.getString("TEXT"),
                rs.getLong("THEORY_ID"),
                rs.getLong("ID")
        )
    }

    companion object {
        private const val COLUMNS_FOR_FETCHING = "ID, THEORY_ID, PATH, PACKAGE_ID, TYPE, TEXT"
        private val log = LoggerFactory.getLogger(DbStatementRepository::class.java)
    }
}


private class StatementSetManager(val scriptName: String, val creationDate: Instant) : SetManager<FixedMasStatement> {
    override fun getInsertStatement() = "INSERT INTO STATEMENT (THEORY_ID, PATH, PACKAGE_ID, SCRIPT, TYPE, TEXT, CREATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?)"

    override fun populateParameters(value: FixedMasStatement, ps: PreparedStatement) {
        ps.setLong(1, value.theoryId)
        ps.setString(2, value.path)
        ps.setLong(3, value.packageId)
        ps.setString(4, this.scriptName)
        ps.setString(5, value.type.name)
        ps.setString(6, value.text)
        ps.setTimestamp(7, Timestamp.from(this.creationDate))
    }

    override fun setGeneratedValues(entity: FixedMasStatement, keys: ResultSet) {
        // entity provides no id at the moment
    }
}
