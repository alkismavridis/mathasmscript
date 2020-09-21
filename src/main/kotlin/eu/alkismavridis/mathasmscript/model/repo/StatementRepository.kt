package eu.alkismavridis.mathasmscript.model.repo

import java.time.Instant

interface StatementRepository {
    fun findByPath(path: String): FixedMasStatement?
    fun findAllByPackage(packageName: String): List<FixedMasStatement>
    fun findExistingNames(fullNames:Collection<String>) : List<String>
    fun findAll(fullNames:Collection<String>) : List<FixedMasStatement>

    fun saveAll(statements:List<FixedMasStatement>, scriptName: String, creationDate: Instant)
    fun update(statement:FixedMasStatement, previousName: String)
}
