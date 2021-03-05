package eu.alkismavridis.mathasmscript.usecases.parser.execute_script

import eu.alkismavridis.mathasmscript.entities.parser.MathasmInspections
import eu.alkismavridis.mathasmscript.entities.parser.result.ParseResult
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.ParseScript
import eu.alkismavridis.mathasmscript.usecases.repo.AssertStatementsNotExisting
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.util.*

class ExecuteScript(private val theoryId: Long, private val stmtRepo: StatementRepository) {

    fun run(script:String) : ParseResult {
        val inspections = MathasmInspections()
        val scriptName = "${UUID.randomUUID()}.mas"
        var packageName = ""

        try {
            val result = ParseScript(this.theoryId, StringReader(script), this.stmtRepo, inspections).run()
            packageName = result.packageName

            AssertStatementsNotExisting.check(this.theoryId, result.exports, this.stmtRepo, inspections)
            return ParseResult(!inspections.hasErrors(), scriptName, result.packageName, result.imports, result.exports, inspections.getEntries())
        } catch (e: Throwable) {
            log.debug("Error while executing script", e)
            inspections.appError(e.message ?: e.javaClass.simpleName)
            return ParseResult(false, scriptName, packageName, emptyList(), emptyList(), inspections.getEntries())
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExecuteScript::class.java)
    }

}
