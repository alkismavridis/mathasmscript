package eu.alkismavridis.mathasmscript.infrastructure.persistence

import eu.alkismavridis.mathasmscript.model.repo.MasPackage
import eu.alkismavridis.mathasmscript.model.repo.PackageRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.util.HashMap


private class PackageSetManager : SetManager<MasPackage> {
    override fun getInsertStatement() = "INSERT INTO PACKAGE (PATH, PARENT_ID, CREATED_AT) VALUES (?, ?, ?)"

    override fun populateParameters(value: MasPackage, ps:PreparedStatement) {
        ps.setString(1, value.path)
        if (value.parentId == null) {
            ps.setNull(2, Types.BIGINT)
        } else {
            ps.setLong(2, value.parentId!!)
        }
        ps.setTimestamp(3, Timestamp.from(value.createdAt))
    }

    override fun setGeneratedValues(entity: MasPackage, keys: ResultSet) {
        entity.id = keys.getLong(1)
    }
}



@Component
class DbPackageRepository(val jdbcTemplate: JdbcTemplate, val namedJdbcTemplate: NamedParameterJdbcTemplate) : PackageRepository {
    companion object {
        private const val COLUMNS_FOR_FETCHING = "ID, PATH, PARENT_ID, CREATED_AT"
        val log = LoggerFactory.getLogger(DbPackageRepository::class.java)
    }

    /// MAPPERS
    private fun map(rs: ResultSet) : MasPackage? {
        if (rs.isClosed) return null

        return MasPackage(
                rs.getString("PATH"),
                rs.getLong("PARENT_ID"),
                rs.getTimestamp("CREATED_AT").toInstant(),
                rs.getLong("ID")
        )
    }


    /// GETTERS
    override fun find(fullName:String) : MasPackage? {
        return this.jdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM PACKAGE WHERE PATH = ? LIMIT 1",
                arrayOf(fullName),
                ResultSetExtractor { rs -> if(rs.next()) this.map(rs) else null}
        )
    }

    override fun findAllByParent(path:String) : List<MasPackage> {
        return this.jdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM PACKAGE WHERE PARENT_ID = (" +
                        "SELECT ID FROM PACKAGE WHERE PATH = ? LIMIT 1" +
                     ")",
                arrayOf(path),
                RowMapper { rs, _ -> this.map(rs) }
        )
    }

    override fun findExistingNames(paths:Collection<String>) : List<MasPackage> {
        val params: MutableMap<String, Any> = HashMap()
        params["fullNames"] = paths

        return this.namedJdbcTemplate.query(
                "SELECT $COLUMNS_FOR_FETCHING FROM PACKAGE WHERE PATH IN (:fullNames) LIMIT ${paths.size}",
                params,
                RowMapper { rs, _ -> this.map(rs)}
        )
    }


    /// INSERTS
    override fun saveAll(packages:List<MasPackage>) {
        JdbcUtils.saveBatch(packages, PackageSetManager(), this.jdbcTemplate)
    }
}
