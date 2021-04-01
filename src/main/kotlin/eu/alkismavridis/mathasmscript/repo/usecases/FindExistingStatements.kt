package eu.alkismavridis.mathasmscript.repo.usecases

import eu.alkismavridis.mathasmscript.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.repo.StatementRepository

fun findExistingStatements(theoryId: Long, statements:Collection<FixedMasStatement>, repo: StatementRepository) : List<String> {
    val fullNames = statements.map { it.path }
    return repo.findExistingNames(fullNames, theoryId)
}
