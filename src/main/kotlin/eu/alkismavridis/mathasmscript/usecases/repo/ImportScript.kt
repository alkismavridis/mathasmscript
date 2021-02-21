package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.parser.ParseResult
import eu.alkismavridis.mathasmscript.entities.parser.MathasmInspections
import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import eu.alkismavridis.mathasmscript.entities.repo.ScriptRepository
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.ParseScript
import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.MasParserResult
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.time.Instant
import java.util.*


class ImportScript(val stmtRepo: StatementRepository, val packageRepo: PackageRepository, val scriptRepo: ScriptRepository) {
    fun run(scriptText: String) : ParseResult {
        val inspections = MathasmInspections()
        val scriptName = "${UUID.randomUUID()}.mas"
        var packageName = ""

        try {
            val result = ParseScript(StringReader(scriptText), this.stmtRepo, inspections).run()
            packageName = result.packageName

            this.assertResultValidity(result, inspections)
            this.saveResult(result, scriptName, scriptText)

            return ParseResult(
                    !inspections.hasErrors(),
                    result.packageName,
                    scriptName,
                    result.exportedStatements,
                    inspections.getEntries()
            )
        } catch (e: Throwable) {
            log.error("Error while importing script> {}", e.message)
            return ParseResult(
                    false,
                    packageName,
                    scriptName,
                    emptyList(),
                    inspections.getEntries()
            )
        }
    }

    private fun saveResult(result: MasParserResult, scriptName: String, scriptText: String) {
        val creationDate = Instant.now()
        val packageToSave = getOrCreatePackage(result.packageName, creationDate, this.packageRepo)
        result.exportedStatements.forEach{ it.packageId = packageToSave.id }

        log.info("Saving script {}", scriptName)
        this.scriptRepo.saveScript(scriptName, result.packageName, scriptText, result.importedIds)

        log.info("Importing {} statements for script {}", result.exportedStatements.size, result.packageName)
        this.stmtRepo.saveAll(ArrayList(result.exportedStatements), scriptName, creationDate)
    }

    private fun assertResultValidity(result: MasParserResult, inspections: MathasmInspections) {
        if (result.exportedStatements.isEmpty()) {
            val errorMessage = "Script does not export anything. Aborting import process."
            inspections.error(errorMessage)
            throw MathAsmException(errorMessage)
        }

        AssertStatementsNotExisting.check(result.exportedStatements, this.stmtRepo, inspections)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImportScript::class.java)
    }
}
