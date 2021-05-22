package eu.alkismavridis.mathasmscript.parser.internal.scope

import eu.alkismavridis.mathasmscript.math.MathAsmStatement
import eu.alkismavridis.mathasmscript.math.MathasmStatementManager
import eu.alkismavridis.mathasmscript.parser.internal.MasParserException
import eu.alkismavridis.mathasmscript.parser.internal.MathasmInspections
import eu.alkismavridis.mathasmscript.parser.internal.SymbolMap
import eu.alkismavridis.mathasmscript.parser.internal.parse.ParseStatementString
import eu.alkismavridis.mathasmscript.parser.internal.toStatementType
import eu.alkismavridis.mathasmscript.parser.internal.token.NameToken
import eu.alkismavridis.mathasmscript.theory.model.FixedMasStatement
import eu.alkismavridis.mathasmscript.theory.repo.StatementRepository
import java.io.StringReader
import java.util.stream.Collectors

class ResolvedImport(
        val statement: MathAsmStatement,
        val fixesStatement: FixedMasStatement,
        val externalUrl: String
)

class ResolveImports(
        private val repository: StatementRepository,
        private val theoryId: Long,
        private val statementManager: MathasmStatementManager
) {
    fun resolve(imports: Collection<MasRepositoryImports>, map: SymbolMap, parseLogger: MathasmInspections): Map<NameToken, ResolvedImport> {
        val result = mutableMapOf<NameToken, ResolvedImport>()

        imports.forEach {
            if (it.url != "") this.importRemote(parseLogger)
            else this.importLocal(it, result, map, parseLogger)
        }

        return result
    }

    private fun importRemote(parseLogger: MathasmInspections) {
        parseLogger.error(-1, -1, "Only local imports are allowed")
    }

    private fun importLocal(repositoryImports: MasRepositoryImports, result: MutableMap<NameToken, ResolvedImport>, map: SymbolMap, parseLogger: MathasmInspections) {
        val fromDb = this.repository.findAll(repositoryImports.variables.values, this.theoryId)
        fromDb.forEach {
            val localName = repositoryImports.getLocalNameFor(it.path)
            if (localName == null) {
                val message = "Internal error: Could not locate local name for full name ${it.path}"
                parseLogger.error(-1, -1, message)
                throw MasParserException(message)
            }

            val toStatement = ParseStatementString(StringReader(it.text), map, statementManager, localName.name, it.type.toStatementType(), '\u0000').parse()
            result[localName] = ResolvedImport(toStatement, it, "")
        }

        this.assertAllImportsArePresent(fromDb, repositoryImports, parseLogger)
    }

    private fun assertAllImportsArePresent(fetched: Collection<FixedMasStatement>, requestedImports: MasRepositoryImports, parseLogger: MathasmInspections) {
        val fetchedFullNames = fetched.stream().map { it.path }.collect(Collectors.toSet())
        requestedImports.variables.forEach {
            if (!fetchedFullNames.contains(it.value)) {
                parseLogger.error(requestedImports.line, requestedImports.column, "Statement ${requestedImports.url}${it.value} is not found.")
            }
        }
    }
}