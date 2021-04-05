package eu.alkismavridis.mathasmscript.parser

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.parser.converters.toStatementType
import eu.alkismavridis.mathasmscript.parser.parse_script.ParseStatementString
import eu.alkismavridis.mathasmscript.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.repo.StatementRepository
import java.io.StringReader
import java.util.stream.Collectors

class ResolvedImport(
        val statement: MutableMathAsmStatement,
        val fixesStatement: FixedMasStatement,
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
                throw ParserException(message)
            }

            val toStatement = ParseStatementString(StringReader(it.text), map, localName.name, it.type.toStatementType(), '\u0000').parse()
            result[localName] = ResolvedImport(toStatement, it, "")
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
