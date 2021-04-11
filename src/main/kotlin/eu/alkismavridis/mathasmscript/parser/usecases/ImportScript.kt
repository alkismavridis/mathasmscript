package eu.alkismavridis.mathasmscript.parser.usecases

import eu.alkismavridis.mathasmscript.parser.internal.MasParserException
import eu.alkismavridis.mathasmscript.parser.internal.MathasmInspections
import eu.alkismavridis.mathasmscript.parser.internal.extractExportedValues
import eu.alkismavridis.mathasmscript.parser.internal.extractImports
import eu.alkismavridis.mathasmscript.parser.internal.parse.MasParser
import eu.alkismavridis.mathasmscript.parser.internal.parse.MasParserResult
import eu.alkismavridis.mathasmscript.parser.model.ParserResult
import eu.alkismavridis.mathasmscript.parser.model.ParserResultStatus
import eu.alkismavridis.mathasmscript.theory.model.MasScript
import eu.alkismavridis.mathasmscript.theory.repo.PackageRepository
import eu.alkismavridis.mathasmscript.theory.repo.ScriptRepository
import eu.alkismavridis.mathasmscript.theory.repo.StatementRepository
import eu.alkismavridis.mathasmscript.theory.usecases.findExistingStatements
import eu.alkismavridis.mathasmscript.theory.usecases.getOrCreatePackage
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.time.Instant
import java.util.*


class ImportScript(
        private val theoryId: Long,
        private val stmtRepo: StatementRepository,
        private val packageRepo: PackageRepository,
        private val scriptRepo: ScriptRepository
) {
    fun run(scriptText: String) : ParserResult {
        val inspections = MathasmInspections()
        val scriptName = "${UUID.randomUUID()}.mas"
        var packageName = ""

        try {
            val result = MasParser(this.theoryId, StringReader(scriptText), this.stmtRepo, inspections).parse()
            packageName = result.packageName

            this.assertResultValidity(result, inspections)
            this.saveResult(result, scriptName, scriptText)

            return ParserResult(
                    if(inspections.hasErrors()) ParserResultStatus.ERROR else ParserResultStatus.IMPORTED,
                    scriptName,
                    result.packageName,
                    result.variables,
                    inspections.getEntries()
            )
        } catch (e: Throwable) {
            log.error("Error while importing script> {}", e.message)
            return ParserResult(
                    ParserResultStatus.ERROR,
                    scriptName,
                    packageName,
                    emptyList(),
                    inspections.getEntries()
            )
        }
    }

    private fun saveResult(result: MasParserResult, scriptName: String, scriptText: String) {
        val creationDate = Instant.now()
        val packageToSave = getOrCreatePackage(this.theoryId, result.packageName, creationDate, this.packageRepo)
        val exports = extractExportedValues(result.variables)
        val imports = extractImports(result.variables)

        exports.forEach{ it.packageId = packageToSave.id }

        log.info("Saving script {}", scriptName)
        val script = MasScript(this.theoryId, scriptText, scriptName, Instant.now())
        this.scriptRepo.saveScript(script, imports)

        log.info("Importing {} statements for script {}", exports.size, result.packageName)
        this.stmtRepo.saveAll(ArrayList(exports), scriptName, creationDate)
    }

    private fun assertResultValidity(result: MasParserResult, inspections: MathasmInspections) {
        val exports = extractExportedValues(result.variables)

        if (exports.isEmpty()) {
            val errorMessage = "Script does not export anything. Aborting import process."
            inspections.error(errorMessage)
            throw MasParserException(errorMessage)
        }

        val existingStatements = findExistingStatements(this.theoryId, exports, this.stmtRepo)
        if (existingStatements.isNotEmpty()) {
            existingStatements.forEach { inspections.error(-1, -1, "Statement $it already exists") }
            throw IllegalArgumentException("Not able to save statements: there are conflicts with existing statements")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImportScript::class.java)
    }
}
