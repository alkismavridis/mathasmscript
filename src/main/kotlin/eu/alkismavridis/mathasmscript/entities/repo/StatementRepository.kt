package eu.alkismavridis.mathasmscript.entities.repo

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement
import java.time.Instant

interface StatementRepository {
    fun findByPath(path: String, theoryId: Long): FixedMasStatement?
    fun findAllByPackage(packageName: String, theoryId: Long): List<FixedMasStatement>
    fun findExistingNames(fullNames:Collection<String>, theoryId: Long) : List<String>
    fun findAll(fullNames:Collection<String>, theoryId: Long) : List<FixedMasStatement>

    fun findDependenciesOf(statementId: Long) : List<FixedMasStatement>

    fun saveAll(statements:List<FixedMasStatement>, scriptName: String, creationDate: Instant)
    fun update(statement: FixedMasStatement, previousName: String)
}
