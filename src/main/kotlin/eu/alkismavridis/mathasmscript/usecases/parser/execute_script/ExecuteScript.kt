package eu.alkismavridis.mathasmscript.usecases.parser.execute_script

import eu.alkismavridis.mathasmscript.model.parser.ParseResult
import eu.alkismavridis.mathasmscript.model.parser.MasLogger
import eu.alkismavridis.mathasmscript.model.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.MasParser
import eu.alkismavridis.mathasmscript.usecases.repo.assert_statements_not_existing.AssertStatementsNotExisting
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.util.*

class ExecuteScript(val stmtRepo: StatementRepository) {
    companion object {
        val log = LoggerFactory.getLogger(ExecuteScript::class.java)
    }

    fun run(script:String) : ParseResult {
        val parseLogger = MasLogger()
        val scriptName = "${UUID.randomUUID()}.mas"
        var packageName = ""

        try {
            val result = MasParser(StringReader(script), this.stmtRepo, parseLogger).parse()
            packageName = result.packageName

            AssertStatementsNotExisting.check(result.exportedStatements, this.stmtRepo, parseLogger)
            return ParseResult(!parseLogger.hasErrors(), result.packageName, scriptName, result.exportedStatements, parseLogger.getComments())
        } catch (e: Throwable) {
            log.debug("Error while executing script", e)
            return ParseResult(false, packageName, scriptName, emptyList(), parseLogger.getComments())
        }
    }
}
