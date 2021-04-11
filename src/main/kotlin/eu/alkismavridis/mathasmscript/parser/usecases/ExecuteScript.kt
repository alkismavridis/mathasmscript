package eu.alkismavridis.mathasmscript.parser.usecases

import eu.alkismavridis.mathasmscript.parser.internal.MathasmInspections
import eu.alkismavridis.mathasmscript.parser.internal.extractExportedValues
import eu.alkismavridis.mathasmscript.parser.internal.parse.MasParser
import eu.alkismavridis.mathasmscript.parser.internal.parse.MasParserException
import eu.alkismavridis.mathasmscript.parser.model.ParserResult
import eu.alkismavridis.mathasmscript.parser.model.ParserResultStatus
import eu.alkismavridis.mathasmscript.theory.repo.StatementRepository
import eu.alkismavridis.mathasmscript.theory.usecases.findExistingStatements
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.util.*

class ExecuteScript(private val theoryId: Long, private val stmtRepo: StatementRepository) {

    fun run(script:String) : ParserResult {
        val inspections = MathasmInspections()
        val scriptName = "${UUID.randomUUID()}.mas"
        var packageName = ""

        try {
            val result = MasParser(this.theoryId, StringReader(script), this.stmtRepo, inspections).parse()
            packageName = result.packageName

            val existingStatements = findExistingStatements(this.theoryId, extractExportedValues(result.variables), this.stmtRepo)
            if (existingStatements.isNotEmpty()) {
                existingStatements.forEach { inspections.error(-1, -1, "Statement $it already exists") }
                throw IllegalArgumentException("Not able to save statements: there are conflicts with existing statements")
            }

            return ParserResult(
                    if(inspections.hasErrors()) ParserResultStatus.ERROR else ParserResultStatus.EXECUTED,
                    scriptName,
                    result.packageName,
                    result.variables,
                    inspections.getEntries()
            )
        } catch (e: Throwable) {
            log.debug("Error while executing script", e)
            if (e !is MasParserException) {
                inspections.appError(e.message ?: e.javaClass.simpleName)
            }
            return ParserResult(ParserResultStatus.ERROR, scriptName, packageName, emptyList(), inspections.getEntries())
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExecuteScript::class.java)
    }
}
