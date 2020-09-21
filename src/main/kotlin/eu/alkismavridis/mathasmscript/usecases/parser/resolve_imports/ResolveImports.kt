package eu.alkismavridis.mathasmscript.usecases.parser.resolve_imports

import eu.alkismavridis.mathasmscript.model.parser.MasRepositoryImports
import eu.alkismavridis.mathasmscript.model.parser.SymbolMap
import eu.alkismavridis.mathasmscript.model.parser.NameToken
import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.model.parser.MasLogger
import eu.alkismavridis.mathasmscript.model.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.model.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_statement_string.ParseStatementString
import java.io.StringReader
import java.util.stream.Collectors

class ResolvedImport(
        val statement: MathAsmStatement,
        val internalId: Long,
        val externalUrl: String
)

class ResolveImports(val repository: StatementRepository) {
    fun resolve(imports: Collection<MasRepositoryImports>, map: SymbolMap, parseLogger: MasLogger) : Map<NameToken, ResolvedImport> {
        val result = mutableMapOf<NameToken, ResolvedImport>()
        val internalImportIds = mutableSetOf<Long>()


        imports.forEach{
            if (it.url != "") this.importRemote(parseLogger)
            else this.importLocal(it, result, map, parseLogger)
        }

        return result
    }

    private fun importRemote(parseLogger: MasLogger) {
        parseLogger.error(-1, -1, "Only local imports are allowed")
    }

    private fun importLocal(repositoryImports: MasRepositoryImports, result: MutableMap<NameToken, ResolvedImport>, map: SymbolMap, parseLogger: MasLogger) {
        val fromDb = this.repository.findAll(repositoryImports.variables.values)
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

    private fun assertAllImportsArePresent(fetched:Collection<FixedMasStatement>, requestedImports: MasRepositoryImports, parseLogger: MasLogger)  {
        val fetchedFullNames = fetched.stream().map{it.path}.collect(Collectors.toSet())
        requestedImports.variables.forEach{
            if (!fetchedFullNames.contains(it.value)) {
                parseLogger.error(requestedImports.line, requestedImports.column, "Statement ${requestedImports.url}${it.value} is not found.")
            }
        }
    }

}
