package eu.alkismavridis.mathasmscript.usecases.repo.assert_statements_not_existing

import eu.alkismavridis.mathasmscript.model.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.parser.MasLogger
import eu.alkismavridis.mathasmscript.model.repo.StatementRepository

class AssertStatementsNotExisting {
    companion object {
        fun check(statements:Collection<FixedMasStatement>, repo: StatementRepository, logger: MasLogger) {
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
