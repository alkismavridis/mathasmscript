package eu.alkismavridis.mathasmscript.parser.parse_script

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.parser.*
import eu.alkismavridis.mathasmscript.parser.converters.toFixedStatementType
import eu.alkismavridis.mathasmscript.parser.result.MasVariable
import eu.alkismavridis.mathasmscript.parser.result.MasVariableKind
import eu.alkismavridis.mathasmscript.repo.FixedMasStatement


class MasScope(private val parent: MasScope?, private val inspections: MathasmInspections) {
    private val declarations = mutableMapOf<String, MasDeclaration>()


    fun declareStatement(nameToken: NameToken, stmt: MutableMathAsmStatement, isPrivate: Boolean) {
        if (this.declarations.containsKey(nameToken.name)) {
            this.inspections.error(nameToken.line, nameToken.column, "Cannot re-declare symbol ${nameToken.name}")
        }

        this.declarations[nameToken.name] = StatementDeclaration(nameToken, stmt, isPrivate)
    }

    fun declareImport(repoUrl: String, fullName: String, localNameToken: NameToken) {
        if (this.declarations.containsKey(localNameToken.name)) {
            this.inspections.error(localNameToken.line, localNameToken.column, "Cannot redeclare symbol ${localNameToken.name}")
            return
        }

        this.declarations[localNameToken.name] = ImportDeclaration(repoUrl, fullName, localNameToken)
    }

    fun requireStatement(name: NameToken): MutableMathAsmStatement {
        val symbol = this.findSymbol(name)
        if (symbol is ImportDeclaration) symbol.markAsUsed()

        return symbol.getStatement(this.inspections)
    }

    private fun findSymbol(nameToken: NameToken): MasDeclaration {
        val symbol = this.declarations[nameToken.name]
        if (symbol != null) return symbol

        if (this.parent != null) {
            return this.parent.findSymbol(nameToken)
        } else {
            val error = "Cannot find symbol ${nameToken.name}"
            this.inspections.error(nameToken.line, nameToken.column, error)
            throw ScopeException(error)
        }
    }

    fun createImportGroup(): Map<String, MasRepositoryImports> {
        val ret = mutableMapOf<String, MasRepositoryImports>()
        this.declarations.forEach lit@{ (_, importDeclaration) ->
            if (importDeclaration !is ImportDeclaration) return@lit

            var repoImport = ret[importDeclaration.repoUrl]
            if (repoImport == null) {
                repoImport = MasRepositoryImports(importDeclaration.repoUrl, importDeclaration.nameToken.line, importDeclaration.nameToken.column)
                ret[importDeclaration.repoUrl] = repoImport
            }

            repoImport.variables[importDeclaration.nameToken] = importDeclaration.fullName
        }

        return ret
    }

    fun getVariables(symbolMap: SymbolMap, packageName: String, theoryId: Long): Collection<MasVariable> {
        return this.declarations.map { it.value.toVariable(it.key, symbolMap, packageName, theoryId, this.inspections) }
    }

    fun resolveImport(localName: NameToken, importData: ResolvedImport) {
        val symbol = this.findSymbol(localName)
        if (symbol !is ImportDeclaration) {
            val error = "Cannot resolve import of a non-imported symbol ${localName.name}"
            this.inspections.error(localName.line, localName.column, error)
            throw ScopeException(error)
        }

        symbol.setData(importData, this.inspections)
    }

    fun assertAllImportsAreUsed() {
        this.declarations.values.forEach {
            if (it is ImportDeclaration) {
                it.assertUsed(this.inspections)
            }
        }
    }

    fun getImports(): Sequence<Pair<String, ResolvedImport>> {
        return this.declarations
                .asSequence()
                .mapNotNull {
                    val value = it.value as? ImportDeclaration ?: return@mapNotNull null
                    val importData = value.importData ?: return@mapNotNull null
                    return@mapNotNull it.key to importData
                }
    }
}

abstract class MasDeclaration(val nameToken: NameToken) {
    abstract fun getStatement(inspections: MathasmInspections): MutableMathAsmStatement
    abstract fun toVariable(name: String, symbolMap: SymbolMap, packageName: String, theoryId: Long, inspections: MathasmInspections): MasVariable
}

private class StatementDeclaration(
        name: NameToken,
        val stmt: MutableMathAsmStatement,
        val isPrivate: Boolean
) : MasDeclaration(name) {

    override fun getStatement(inspections: MathasmInspections) = this.stmt

    override fun toVariable(name: String, symbolMap: SymbolMap, packageName: String, theoryId: Long, inspections: MathasmInspections): MasVariable {
        val kind = if (this.isPrivate) MasVariableKind.LOCAL else MasVariableKind.EXPORT
        val fixedStmt = FixedMasStatement(
                path = "${packageName}.${this.stmt.name}",
                packageId = -1L,
                type = this.stmt.type.toFixedStatementType(),
                text = this.stmt.toStatementString(symbolMap::requireSymbol),
                theoryId = theoryId,
                id = -1L
        )

        return MasVariable(kind, name, fixedStmt, "")
    }
}

private class ImportDeclaration(val repoUrl: String, val fullName: String, localName: NameToken) : MasDeclaration(localName) {
    var importData: ResolvedImport? = null
    private var isUsed = false

    override fun getStatement(inspections: MathasmInspections): MutableMathAsmStatement {
        this.assertImportDataPresent(inspections)
        return this.importData!!.statement
    }

    override fun toVariable(name: String, symbolMap: SymbolMap, packageName: String, theoryId: Long, inspections: MathasmInspections): MasVariable {
        this.assertImportDataPresent(inspections)
        return MasVariable(
                MasVariableKind.IMPORT,
                name,
                this.importData!!.fixesStatement,
                this.importData!!.externalUrl
        )
    }

    private fun assertImportDataPresent(inspections: MathasmInspections) {
        if (this.importData == null) {
            val error = "Import statement ${this.nameToken.name} is not resolved"
            inspections.error(this.nameToken.line, this.nameToken.column, error)
            throw ScopeException(error)
        }
    }

    fun setData(importData: ResolvedImport, inspections: MathasmInspections) {
        if (this.importData != null) {
            inspections.error(this.nameToken.line, this.nameToken.column, "Cannot set value for import symbol ${this.nameToken.name}. It already contains a statement.")
        }

        this.importData = importData
    }

    fun markAsUsed() {
        this.isUsed = true
    }

    fun assertUsed(inspections: MathasmInspections) {
        if (!this.isUsed) {
            inspections.error(this.nameToken.line, this.nameToken.column, "Unused import statement detected: ${this.nameToken.name}")
        }
    }
}

private class ScopeException(message: String) : ParserException(message)
