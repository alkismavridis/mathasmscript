package eu.alkismavridis.mathasmscript.infrastructure.persistence

import org.springframework.jdbc.core.ConnectionCallback
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.sql.Types


class TestEntity(
        var id:Long,
        val foo:String,
        val bar: String
)





interface SetManager<T> {
    fun getInsertStatement(): String

    fun populateParameters(value: T, ps:PreparedStatement)

    fun setGeneratedValues(entity: T, keys: ResultSet)
}

class JdbcUtils {
    companion object {
        fun <T> saveBatch(data: List<T>, manager: SetManager<T>, jdbcTemplate: JdbcTemplate) {
            if (data.isEmpty()) return

            jdbcTemplate.execute(ConnectionCallback {conn ->
                conn.prepareStatement(manager.getInsertStatement(), RETURN_GENERATED_KEYS).use { stmt ->
                    conn.autoCommit = false

                    data.forEach {entity ->
                        manager.populateParameters(entity, stmt)
                        stmt.addBatch()
                    }

                    stmt.executeBatch()

                    val keys = stmt.generatedKeys
                    if (keys != null) {
                        data.forEach {entity ->
                            if (!keys.next()) return@forEach
                            manager.setGeneratedValues(entity, keys)
                        }
                    }

                    conn.commit()
                }
            })
        }


        fun setIdOrNull(position: Int, value: Long, ps:PreparedStatement) {
            if (value == -1L) {
                ps.setNull(position, Types.BIGINT)
            } else {
                ps.setLong(position, value)
            }
        }
    }
}
