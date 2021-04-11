package eu.alkismavridis.mathasmscript.theory.usecases

import eu.alkismavridis.mathasmscript.theory.model.FixedMasStatement
import eu.alkismavridis.mathasmscript.theory.repo.StatementRepository

fun findExistingStatements(theoryId: Long, statements:Collection<FixedMasStatement>, repo: StatementRepository) : List<String> {
    val fullNames = statements.map { it.path }
    return repo.findExistingNames(fullNames, theoryId)
}
