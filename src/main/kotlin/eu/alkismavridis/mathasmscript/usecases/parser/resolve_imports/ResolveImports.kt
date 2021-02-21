package eu.alkismavridis.mathasmscript.usecases.parser.resolve_imports

import eu.alkismavridis.mathasmscript.entities.parser.MasRepositoryImports
import eu.alkismavridis.mathasmscript.entities.parser.SymbolMap
import eu.alkismavridis.mathasmscript.entities.parser.NameToken
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.entities.parser.MathasmInspections
import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_statement_string.ParseStatementString
import java.io.StringReader
import java.util.stream.Collectors

class ResolvedImport(
        val statement: MathAsmStatement,
        val internalId: Long,
        val externalUrl: String
)

class ResolveImports(private val repository: StatementRepository, private val theoryId: Long) {
    fun resolve(imports: Collection<MasRepositoryImports>, map: SymbolMap, parseLogger: MathasmInspections) : Map<NameToken, ResolvedImport> {
        val result = mutableMapOf<NameToken, ResolvedImport>()

        imports.forEach{
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
                throw MathAsmException(message)
            }

            val toStatement = ParseStatementString(StringReader(it.text), map, localName.name, it.type, '\u0000').parse()
            result[localName] = ResolvedImport(toStatement, it.id, "")
        }

        this.assertAllImportsArePresent(fromDb, repositoryImports, parseLogger)
    }

    private fun assertAllImportsArePresent(fetched:Collection<FixedMasStatement>, requestedImports: MasRepositoryImports, parseLogger: MathasmInspections)  {
        val fetchedFullNames = fetched.stream().map{it.path}.collect(Collectors.toSet())
        requestedImports.variables.forEach{
            if (!fetchedFullNames.contains(it.value)) {
                parseLogger.error(requestedImports.line, requestedImports.column, "Statement ${requestedImports.url}${it.value} is not found.")
            }
        }
    }

}
