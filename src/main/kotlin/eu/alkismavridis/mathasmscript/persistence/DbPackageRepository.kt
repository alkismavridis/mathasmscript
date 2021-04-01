package eu.alkismavridis.mathasmscript.persistence

import eu.alkismavridis.mathasmscript.repo.MasPackage
import eu.alkismavridis.mathasmscript.repo.PackageRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.util.*

@Component
class DbPackageRepository(val jdbcTemplate: JdbcTemplate, val namedJdbcTemplate: NamedParameterJdbcTemplate) : PackageRepository {
    /// GETTERS
    override fun find(fullName:String, theoryId: Long) : MasPackage? {
        return this.jdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM PACKAGE WHERE PATH = ? AND THEORY_ID = ? LIMIT 1",
                arrayOf(fullName, theoryId),
                ResultSetExtractor { rs -> if(rs.next()) this.map(rs) else null}
        )
    }

    override fun findAllByParent(path:String, theoryId: Long) : List<MasPackage> {
        return this.jdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM PACKAGE WHERE PARENT_ID = (" +
                        "SELECT ID FROM PACKAGE WHERE PATH = ? AND THEORY_ID = ? LIMIT 1" +
                     ")",
                arrayOf(path, theoryId)
        ) { rs, _ -> this.map(rs) }
    }

    override fun findExistingNames(paths:Collection<String>, theoryId: Long) : List<MasPackage> {
        val params: MutableMap<String, Any> = HashMap()
        params["fullNames"] = paths
        params["theoryId"] = theoryId

        return this.namedJdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM PACKAGE WHERE THEORY_ID = :theoryId AND PATH IN (:fullNames) LIMIT ${paths.size}",
                params
        ) { rs, _ -> this.map(rs) }
    }


    /// INSERTS
    override fun saveAll(packages:List<MasPackage>) {
        JdbcUtils.saveBatch(packages, PackageSetManager(), this.jdbcTemplate)
    }

    override fun existsByParent(path: String, theoryId: Long): Boolean {
        val pattern = if (path.isEmpty()) "%" else "$path.%"

        return this.jdbcTemplate.query(
                "SELECT ID FROM PACKAGE WHERE THEORY_ID = ? AND PATH LIKE ? LIMIT 1",
                arrayOf(theoryId, pattern),
                ResultSetExtractor { rs -> rs.next() }
        ) ?: false
    }

    override fun delete(path: String, theoryId: Long): Boolean {
        val rowsAffected = this.jdbcTemplate.update(
                "DELETE FROM PACKAGE WHERE THEORY_ID = ? AND PATH = ? LIMIT 1",
                theoryId,
                path
        )

        return rowsAffected > 0
    }

    private fun map(rs: ResultSet) : MasPackage? {
        if (rs.isClosed) return null

        return MasPackage(
                rs.getLong("ID"),
                rs.getLong("THEORY_ID"),
                rs.getString("PATH"),
                rs.getLong("PARENT_ID"),
                rs.getTimestamp("CREATED_AT").toInstant()
        )
    }

    companion object {
        private const val COLUMNS_FOR_FETCHING = "ID, THEORY_ID, PATH, PARENT_ID, CREATED_AT"
    }
}

private class PackageSetManager : SetManager<MasPackage> {
    override fun getInsertStatement() = "INSERT INTO PACKAGE (THEORY_ID, PATH, PARENT_ID, CREATED_AT) VALUES (?, ?, ?, ?)"

    override fun populateParameters(value: MasPackage, ps:PreparedStatement) {
        ps.setLong(1, value.theoryId)
        ps.setString(2, value.path)
        if (value.parentId == null) {
            ps.setNull(3, Types.BIGINT)
        } else {
            ps.setLong(3, value.parentId)
        }
        ps.setTimestamp(4, Timestamp.from(value.createdAt))
    }

    override fun setGeneratedValues(entity: MasPackage, keys: ResultSet) {
        entity.id = keys.getLong(1)
    }
}
