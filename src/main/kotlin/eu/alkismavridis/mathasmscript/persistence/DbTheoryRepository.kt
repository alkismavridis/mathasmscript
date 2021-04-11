package eu.alkismavridis.mathasmscript.persistence

import eu.alkismavridis.mathasmscript.theory.model.Theory
import eu.alkismavridis.mathasmscript.theory.repo.TheoryRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet


@Component
class DbTheoryRepository(val jdbcTemplate: JdbcTemplate) : TheoryRepository {
    override fun findAll(): List<Theory> {
        return this.jdbcTemplate.query("SELECT $ALL_COLUMNS FROM $TABLE_NAME") { rs, _ ->
            this.map(rs)
        }
    }

    private fun map(rs: ResultSet): Theory? {
        if (rs.isClosed) return null

        return Theory(
                rs.getLong("ID"),
                rs.getString("NAME"),
                rs.getTimestamp("CREATED_AT").toInstant()
        )
    }

    companion object {
        private const val TABLE_NAME = "THEORY"
        private const val ALL_COLUMNS = "ID, NAME, CREATED_AT"
    }
}
