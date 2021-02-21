package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.parser.MathasmInspections
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository

class AssertStatementsNotExisting {
    companion object {
        fun check(statements:Collection<FixedMasStatement>, repo: StatementRepository, logger: MathasmInspections) {
            val fullNames = statements.map{it.path}
            val existingInRepository = repo.findExistingNames(fullNames)
            if (existingInRepository.isEmpty()) {
                return
            }

            existingInRepository.forEach { logger.error(-1, -1, "Statement $it already exists") }
            throw MathAsmException("Not able to save statements: there are conflicts with existing statements")
        }
    }
}