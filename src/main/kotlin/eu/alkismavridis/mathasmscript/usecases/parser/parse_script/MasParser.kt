package eu.alkismavridis.mathasmscript.usecases.parser.parse_script

import eu.alkismavridis.mathasmscript.model.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.model.parser.SymbolMap
import eu.alkismavridis.mathasmscript.model.logic.*
import eu.alkismavridis.mathasmscript.model.parser.*
import eu.alkismavridis.mathasmscript.model.parser.MasLogger
import eu.alkismavridis.mathasmscript.model.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.parse_statement_string.ParseStatementString
import eu.alkismavridis.mathasmscript.usecases.parser.read_token.MasTokenizer
import eu.alkismavridis.mathasmscript.usecases.parser.validate_package_part_name.ValidatePackagePartName
import eu.alkismavridis.mathasmscript.usecases.parser.validate_theorem_name.ValidateStatementName
import eu.alkismavridis.mathasmscript.usecases.logic.replace_all.replaceAll
import eu.alkismavridis.mathasmscript.usecases.logic.replace_in_sentence.replaceAllInSentence
import eu.alkismavridis.mathasmscript.usecases.logic.replace_single_match.replaceSingleMatch
import eu.alkismavridis.mathasmscript.usecases.logic.revert_statement.revertStatement
import eu.alkismavridis.mathasmscript.usecases.logic.stabilize_open_theorem.stabilizeOpenTheorem
import eu.alkismavridis.mathasmscript.usecases.logic.start_theorem.startTheorem
import eu.alkismavridis.mathasmscript.usecases.parser.resolve_imports.ResolveImports
import java.io.Reader
import java.io.StringReader
import java.lang.StringBuilder

class ImportId(
        val internalId:Long,
        val externalId:String
)

class MasParserResult(
        val packageName:String,
        val exportedStatements: Collection<FixedMasStatement>,
        val importedIds: List<ImportId>
)

private class TokenLineInfo(val linesBeforeToken:Int, val token: MasToken)

class MasParser(reader: Reader, private val stmtRepo: StatementRepository, private var logger: MasLogger) {
    companion object {
        const val ANONYMOUS_EXPRESSION_NAME = "AnonymousExpression"
    }

    private val tokenizer = MasTokenizer(reader, logger)

    /** Import statements are allowed before any proof or axiom definitions */
    private var packageName = ""
    private val scope = MasScope(null, logger)
    private val symbolMap = SymbolMap()
    private var rolledBackToken: MasToken? = null
    private var logicSelection = LogicSelection(100)

    private var hasResolvedImports = false
    private var hasDeclaredTheorem = false


    /// STATEMENT PARSING
    fun parse() : MasParserResult {
        while (this.parseNextStatement()) { /* continue parsing until we hit the end */ }
        this.scope.assertAllImportsAreUsed()

        return MasParserResult(
                this.packageName,
                this.scope.getExportableStatements().map{
                    FixedMasStatement(
                            "${this.packageName}.${it.name}",
                            -1L,
                            it.type,
                            this.symbolMap.toString(it),
                            -1L
                    )
                },
                this.scope.getImportIds()
        )
    }

    /** Returns true if parsing should continue. */
    private fun parseNextStatement() : Boolean {
        val nextToken = this.getNextNonNL()
        if (nextToken.type == MasTokenType.EOF) {
            return false
        }

        if (nextToken.type != MasTokenType.NAME) {
            throw this.addError(nextToken, "Expected package, import, export, axiom or theorem, but found ${nextToken.getTextRepresentation()}")
        }

        val asIdToken = nextToken as NameToken
        when(asIdToken.name) {
            "package" -> this.parsePackageStatement(asIdToken)
            "import" -> this.parseImportStatement(asIdToken, "")
            "axiom" -> this.parseAxiomStatement(asIdToken)
            "theorem" -> this.parseTheoremStatement(true, asIdToken)
            "export" -> {
                this.requireTokenWithText("theorem")
                this.parseTheoremStatement(false, asIdToken)
            }
            else -> throw this.addError(asIdToken, "Expected package, import, export, axiom or theorem, but found ${asIdToken.name}")
        }

        return true
    }

    private fun parsePackageStatement(fileToken: NameToken) {
        if (this.packageName != "") {
            throw this.addError(fileToken, "Illegal package statement. Only one is allowed.")
        }

        this.packageName = this.readPackageName(true)
    }

    private fun readPackageName(requireEndOfLine: Boolean) : String {
        val firstPackagePart = this.requireIdentifier()
        if (!ValidatePackagePartName.isPackagePartNameValid(firstPackagePart.name)) {
            throw this.addError(firstPackagePart, ValidatePackagePartName.ERROR_MESSAGE)
        }
        val builder = StringBuilder(firstPackagePart.name)

        while (true) {
            val nextToken = this.getNextToken()
            if (nextToken.type == MasTokenType.NEW_LINE || nextToken.type == MasTokenType.EOF) {
                break
            } else if (nextToken.type == MasTokenType.DOT) {
                val nextPackagePart = this.requireIdentifier()
                if (!ValidatePackagePartName.isPackagePartNameValid(nextPackagePart.name)) {
                    throw this.addError(nextPackagePart, ValidatePackagePartName.ERROR_MESSAGE)
                }
                builder.append(".").append(nextPackagePart.name)
                continue
            }

            if (requireEndOfLine) {
                throw this.addError(nextToken,"Expected end of line after package statement")
            }
        }

        return builder.toString()
    }

    private fun parseImportStatement(openingStatementToken: MasToken, repoUrl:String) {
        if (this.packageName == "") {
            throw this.addError(openingStatementToken, "Imports must declared after package statement")
        }

        if (this.hasResolvedImports) {
            throw this.addError(openingStatementToken, "Imports must precede all axiom and theorem definitions")
        }

        val firstPackagePart = this.requireIdentifier()
        val builder = StringBuilder(firstPackagePart.name)
        var lastPartToken = firstPackagePart

        while (true) {
            val nextTokenInfo = this.countLinesAndGetToken()
            if (nextTokenInfo.token.type == MasTokenType.DOT) {
                val nextPackagePart = this.requireIdentifier()
                builder.append(".").append(nextPackagePart.name)
                lastPartToken = nextPackagePart
            } else if (nextTokenInfo.token.type == MasTokenType.NAME && (nextTokenInfo.token as NameToken).name == "as") {
                val localNameToken = this.requireIdentifier()
                this.scope.declareImport(repoUrl, builder.toString(), localNameToken)
                this.requireNewLineOrEof()
                return
            } else if (nextTokenInfo.token.type == MasTokenType.CURLY_OPEN) {
                val paramsToImport = this.parseImportVariables()
                val packageName = builder.toString()
                paramsToImport.forEach{ (localNameToken, officialNameToken) ->
                    this.scope.declareImport(repoUrl, "$packageName.${officialNameToken.name}", localNameToken)
                }
                this.requireNewLineOrEof()
                return
            } else {
                if (nextTokenInfo.linesBeforeToken < 1) {
                    throw this.addError(nextTokenInfo.token, "Expected end of line after import statement but found ${nextTokenInfo.token.getTextRepresentation()}")
                }

                this.scope.declareImport(repoUrl, builder.toString(), lastPartToken)
                this.rolledBackToken = nextTokenInfo.token
                return
            }
        }
    }

    private fun parseImportVariables() : Map<NameToken, NameToken> {
        val result = mutableMapOf<NameToken, NameToken>() //localName: officialName
        while(true) {
            val next = this.getNextNonNL()
            if (next.type == MasTokenType.CURLY_CLOSE) break

            if (next.type != MasTokenType.NAME) {
                throw this.addError(next, "Expected identifier, but found ${next.getTextRepresentation()}")
            }

            val officialNameToken = next as NameToken
            val tokenAfterName = this.getNextNonNL()
            when(tokenAfterName.type) {
                MasTokenType.NAME -> {
                    if ((tokenAfterName as NameToken).name != "as") {
                        throw this.addError(tokenAfterName, "Expected as , or } but found ${tokenAfterName.getTextRepresentation()}")
                    }

                    val localNameToken = this.requireIdentifier()
                    result[localNameToken] = officialNameToken

                    val tokenAfterOfficialName = this.getNextNonNL()
                    when(tokenAfterOfficialName.type) {
                        MasTokenType.COMMA -> { /*just continue*/ }
                        MasTokenType.CURLY_CLOSE -> {
                            this.rolledBackToken = tokenAfterOfficialName
                        }
                        else -> throw this.addError(tokenAfterOfficialName, "Expected , or }, but found ${tokenAfterOfficialName.getTextRepresentation()}")
                    }
                }
                MasTokenType.COMMA -> {
                    result[officialNameToken] = officialNameToken
                }
                MasTokenType.CURLY_CLOSE -> {
                    result[officialNameToken] = officialNameToken
                    this.rolledBackToken = tokenAfterName
                }
                else -> throw this.addError(tokenAfterName, "Expected , } or :, but found ${tokenAfterName.getTextRepresentation()}")
            }
        }

        return result
    }

    private fun parseAxiomStatement(openingStatementToken: NameToken) {
        if (this.packageName == "") {
            throw this.addError(openingStatementToken, "Cannot declare axiom before declaring package")
        }

        if (this.hasDeclaredTheorem) {
            throw this.addError(openingStatementToken, "Axioms must be declared before theorems")
        }

        this.resolveImports()

        val nameToken = this.requireIdentifier()
        if (!ValidateStatementName.isAxiomNameValid(nameToken.name)) {
            throw this.addError(nameToken, ValidateStatementName.INVALID_AXIOM_MESSAGE)
        }
        this.requireTokenOfType(MasTokenType.EQUALS)
        val axiomStringToken = this.requireString()
        this.requireNewLineOrEof()

        val axiom = ParseStatementString(StringReader(axiomStringToken.text), this.symbolMap, nameToken.name, StatementType.AXIOM, '"').parse()
        this.scope.declareStatement(nameToken, axiom, false)
    }

    private fun parseTheoremStatement(isPrivate: Boolean, openingStatementToken: MasToken) {
        if (this.packageName == "") {
            throw this.addError(openingStatementToken, "Cannot declare theorem before declaring package")
        }

        val nameToken = this.requireIdentifier()
        if (!ValidateStatementName.isTheoremNameValid(nameToken.name)) {
            throw this.addError(nameToken, ValidateStatementName.INVALID_THEOREM_MESSAGE)
        }

        val nextToken = this.getNextNonNL()
        when (nextToken.type) {
            MasTokenType.EQUALS -> {
                val expressionResult = this.parseTheoremExpression(nameToken.name, true)

                val stabilizedResult = stabilizeOpenTheorem(expressionResult)
                this.scope.declareStatement(nameToken, stabilizedResult, isPrivate)
            }
            else -> throw this.addError(nextToken, "Expected = after theorem name")
        }

        this.hasDeclaredTheorem = true
    }


    /// TOKEN GETTERS
    private fun requireIdentifier() : NameToken {
        val nextToken = this.getNextNonNL()
        if (nextToken.type != MasTokenType.NAME) {
            throw this.addError(nextToken, "Expected identifier but found ${nextToken.getTextRepresentation()}")
        }

        return nextToken as NameToken
    }

    private fun requireTokenWithText(text:String) : NameToken {
        val nextToken = this.requireIdentifier()
        if (text != nextToken.name) {
            throw this.addError(nextToken, "Expected $text, but found ${nextToken.getTextRepresentation()}")
        }

        return nextToken
    }

    private fun requireNewLineOrEof() : MasToken {
        val nextToken = this.getNextToken()
        if (nextToken.type != MasTokenType.EOF && nextToken.type != MasTokenType.NEW_LINE) {
            throw this.addError(nextToken, "Expected new line or end of file, but found ${nextToken.getTextRepresentation()}")
        }

        return nextToken
    }

    private fun requireString() : StringToken {
        val nextToken = this.getNextNonNL()
        if (nextToken.type != MasTokenType.STRING) {
            throw this.addError(nextToken, "Expected string, but found ${nextToken.getTextRepresentation()}")
        }

        return nextToken as StringToken
    }

    private fun requireTokenOfType(type: MasTokenType) : MasToken {
        val nextToken = this.getNextNonNL()
        if (nextToken.type != type) {
            throw this.addError(nextToken, "Expected token of type ${type.name}, but found ${nextToken.getTextRepresentation()}")
        }

        return nextToken
    }

    private fun getNextNonNL() : MasToken {
        while(true) {
            val nextToken = this.getNextToken()
            if (nextToken.type != MasTokenType.NEW_LINE) return nextToken
        }
    }

    private fun countLinesAndGetToken() : TokenLineInfo {
        var linesEncountered = 0
        while(true) {
            val nextToken = this.getNextToken()
            if (nextToken.type != MasTokenType.NEW_LINE) return TokenLineInfo(linesEncountered, nextToken)
            else linesEncountered++
        }
    }

    private fun getNextToken() : MasToken {
        if (this.rolledBackToken != null) {
            val ret = this.rolledBackToken
            this.rolledBackToken = null
            return ret!!
        }
        return this.tokenizer.nextToken()
    }



    /// THEOREM PARSING
    private fun parseTheoremExpression(name:String, requireEndOfLine:Boolean) : MathAsmStatement {
        this.resolveImports()

        val baseId = this.requireIdentifier()
        var currentTarget = this.scope.requireStatement(baseId)

        // Execute .foo() chain
        while(true) {
            val tokenInfo = this.countLinesAndGetToken()
            if (tokenInfo.token.type != MasTokenType.DOT) {
                if (requireEndOfLine && tokenInfo.linesBeforeToken < 1  && tokenInfo.token.type != MasTokenType.EOF) {
                    throw this.addError(tokenInfo.token, "End of line expected before ${tokenInfo.token.getTextRepresentation()}")
                } else {
                    this.rolledBackToken = tokenInfo.token
                    break
                }
            } else {
                currentTarget = this.parseTransformation(currentTarget, name)
            }
        }

        return currentTarget
    }

    private fun parseTransformation(target:MathAsmStatement, name:String) : MathAsmStatement {
        val methodName = this.requireIdentifier()
        when(methodName.name) {
            "all" -> {
                this.requireTokenOfType(MasTokenType.PARENTHESIS_OPEN)
                val baseParameter = this.parseTheoremExpression(ANONYMOUS_EXPRESSION_NAME, false)
                this.requireTokenOfType(MasTokenType.PARENTHESIS_CLOSE)

                return replaceAll(target, baseParameter, this.logicSelection, name)
            }
            "right" -> return this.parseSentenceOrSingleReplacement(target, StatementSide.RIGHT, name)
            "left" -> return this.parseSentenceOrSingleReplacement(target, StatementSide.LEFT, name)
            "reverse" -> {
                this.requireTokenOfType(MasTokenType.PARENTHESIS_OPEN)
                this.requireTokenOfType(MasTokenType.PARENTHESIS_CLOSE)
                return revertStatement(target)
            }
            "cloneLeft" -> {
                this.requireTokenOfType(MasTokenType.PARENTHESIS_OPEN)
                this.requireTokenOfType(MasTokenType.PARENTHESIS_CLOSE)
                return startTheorem(name, target, StatementSide.LEFT)
            }
            "cloneRight" -> {
                this.requireTokenOfType(MasTokenType.PARENTHESIS_OPEN)
                this.requireTokenOfType(MasTokenType.PARENTHESIS_CLOSE)
                return startTheorem(name, target, StatementSide.RIGHT)
            }
            else -> throw this.addError(methodName, "Unknown function ${methodName.name}")
        }
    }

    private fun parseSentenceOrSingleReplacement(target:MathAsmStatement, side:StatementSide, name:String) : MathAsmStatement {
        this.requireTokenOfType(MasTokenType.PARENTHESIS_OPEN)
        val base = this.parseTheoremExpression(ANONYMOUS_EXPRESSION_NAME, false)

        val nextToken = this.getNextNonNL()
        return when(nextToken.type) {
            MasTokenType.PARENTHESIS_CLOSE -> {
                replaceAllInSentence(target, side, base, this.logicSelection, name)
            }

            MasTokenType.COMMA -> {
                val position = this.parsePosition()
                this.requireTokenOfType(MasTokenType.PARENTHESIS_CLOSE)
                replaceSingleMatch(target, side, position, base, this.logicSelection, name)
            }

            else -> throw this.addError(nextToken, "Expected , or ), but found ${nextToken.getTextRepresentation()}")
        }
    }

    private fun parsePosition() : Int {
        val token = this.requireTokenOfType(MasTokenType.NUMBER)
        return (token as NumberToken).value
    }

    /// IMPORT MANAGEMENT
    private fun resolveImports() {
        if (this.hasResolvedImports) return

        val repositoryImports = this.scope.createImportGroup()
        val result = ResolveImports(this.stmtRepo).resolve(repositoryImports.values, this.symbolMap, this.logger)
        result.forEach{(localName, importData) -> this.scope.resolveImport(localName, importData)}

        this.hasResolvedImports = true
    }


    /// ERROR HANDLING
    private fun addError(token: MasToken, message:String) : MasParserException {
        this.logger.error(token.line, token.column, message)
        return MasParserException("Line ${token.line}_${token.column}: $message")
    }
}
