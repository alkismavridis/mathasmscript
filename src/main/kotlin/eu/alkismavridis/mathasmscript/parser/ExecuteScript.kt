package eu.alkismavridis.mathasmscript.parser

import eu.alkismavridis.mathasmscript.old.usecases.parser.extractExportedValues
import eu.alkismavridis.mathasmscript.parser.parse_script.MasParserException
import eu.alkismavridis.mathasmscript.parser.parse_script.ParseScript
import eu.alkismavridis.mathasmscript.parser.result.ParserResult
import eu.alkismavridis.mathasmscript.parser.result.ParserResultStatus
import eu.alkismavridis.mathasmscript.repo.StatementRepository
import eu.alkismavridis.mathasmscript.repo.usecases.assertStatementsNotExisting
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.util.*

class ExecuteScript(private val theoryId: Long, private val stmtRepo: StatementRepository) {

    fun run(script:String) : ParserResult {
        val inspections = MathasmInspections()
        val scriptName = "${UUID.randomUUID()}.mas"
        var packageName = ""

        try {
            val result = ParseScript(this.theoryId, StringReader(script), this.stmtRepo, inspections).run()
            packageName = result.packageName

            assertStatementsNotExisting(this.theoryId, extractExportedValues(result.variables), this.stmtRepo, inspections)
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