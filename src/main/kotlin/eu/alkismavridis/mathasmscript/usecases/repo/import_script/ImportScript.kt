package eu.alkismavridis.mathasmscript.usecases.repo.import_script

import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.parser.ParseResult
import eu.alkismavridis.mathasmscript.model.parser.MasLogger
import eu.alkismavridis.mathasmscript.model.repo.PackageRepository
import eu.alkismavridis.mathasmscript.model.repo.ScriptRepository
import eu.alkismavridis.mathasmscript.model.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.MasParser
import eu.alkismavridis.mathasmscript.usecases.repo.assert_statements_not_existing.AssertStatementsNotExisting
import eu.alkismavridis.mathasmscript.usecases.repo.save_package.GetOrCreatePackage
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.time.Instant
import java.util.*


class ImportScript(val stmtRepo: StatementRepository, val packageRepo: PackageRepository, val scriptRepo: ScriptRepository) {
    companion object {
        private val log = LoggerFactory.getLogger(ImportScript::class.java)
    }

    fun run(script: String) : ParseResult {
        val parseLogger = MasLogger()
        var packageName = ""
        val scriptName = "${UUID.randomUUID()}.mas"

        try {
            val result = MasParser(StringReader(script), this.stmtRepo, parseLogger).parse()
            packageName = result.packageName
            AssertStatementsNotExisting.check(result.exportedStatements, this.stmtRepo, parseLogger)

            if (result.exportedStatements.isEmpty()) {
                val errorMessage = "Script does not export anything. Abordting import process."
                parseLogger.error(errorMessage)
                throw MathAsmException(errorMessage)
            }

            val creationDate = Instant.now()
            val packageToSave = GetOrCreatePackage.get(result.packageName, creationDate, this.packageRepo)
            result.exportedStatements.forEach{ it.packageId = packageToSave.id }

            log.info("Saving script {}", scriptName)
            this.scriptRepo.saveScript(scriptName, result.packageName, script, result.importedIds)

            log.info("Importing {} statements for script {}", result.exportedStatements.size, result.packageName)
            this.stmtRepo.saveAll(ArrayList(result.exportedStatements), scriptName, creationDate)

            return ParseResult(!parseLogger.hasErrors(), result.packageName, scriptName, result.exportedStatements, parseLogger.getComments())
        } catch (e: Throwable) {
            log.error("Oopsie", e)
            return ParseResult(
                    false,
                    packageName,
                    scriptName,
                    emptyList(),
                    parseLogger.getComments()
            )
        }
    }
}
