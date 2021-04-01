package eu.alkismavridis.mathasmscript.repo

import eu.alkismavridis.mathasmscript.core.FixedMasStatement
import java.time.Instant

interface StatementRepository {
    fun findByPath(path: String, theoryId: Long): FixedMasStatement?
    fun findAllByPackage(packageName: String, theoryId: Long): List<FixedMasStatement>
    fun findExistingNames(fullNames:Collection<String>, theoryId: Long) : List<String>
    fun findAll(fullNames:Collection<String>, theoryId: Long) : List<FixedMasStatement>

    fun findDependenciesOf(statementId: Long) : List<FixedMasStatement>
    fun hasDependencies(statementId: Long): Boolean

    fun saveAll(statements:List<FixedMasStatement>, scriptName: String, creationDate: Instant)
    fun update(statement: FixedMasStatement, previousName: String)
    fun existsByParent(path: String, theoryId: Long): Boolean
}
