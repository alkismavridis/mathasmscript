package eu.alkismavridis.mathasmscript.parser.internal

import eu.alkismavridis.mathasmscript.math.MathAsmStatement
import eu.alkismavridis.mathasmscript.math.MathasmStatementManager
import eu.alkismavridis.mathasmscript.math.StatementType
import java.io.Reader


class ParseStatementString(
        private val reader:Reader,
        private val symbolMap: SymbolMap,
        private val manager: MathasmStatementManager,
        private val name:String,
        private val type: StatementType,
        private val endingChar:Char) {

    var rolledBackCharacter:Char? = null
    var grade = -1
    var isBidirectional = false
    private var left = mutableListOf<Long>()
    private var right = mutableListOf<Long>()


    fun parse() : MathAsmStatement {
        while (true) {
            val firstChar = readNextNonWhiteNorNewLine()

            when(firstChar) {
                endingChar, '\u0000' -> return this.commitStatementCreation()
            }

            val nextToken = this.getNextToken(firstChar)
            this.integrateToken(nextToken)
        }
    }


    /// STATEMENT BUILDING
    private fun commitStatementCreation() : MathAsmStatement {
        if(this.grade == -1) {
            throw ParseAxiomException("Cannot create an axiom ${this.name} without a connection")
        }

        if(this.left.isEmpty()) {
            throw ParseAxiomException("Left sentence cannot be empty")
        }

        if(this.right.isEmpty()) {
            throw ParseAxiomException("Right sentence cannot be empty")
        }

        return manager.createStatement(
                this.name,
                this.type,
                this.left.toLongArray(),
                this.right.toLongArray(),
                this.isBidirectional,
                this.grade
        )
    }

    private fun integrateToken(token:String) {
        if (!token.endsWith("-->")) {
            this.integrateSymbol(token)
            return
        }

        if (token == "<--->") {
            this.integrateConnection(0, true)
        } else if (token == "--->") {
            this.integrateConnection(0, false)
        } else if (token.length >= 7 && token.startsWith("<--")) {
            val grade = token.substring(3, token.lastIndex - 2)
            this.integrateConnection(grade, true)
        } else if (token.length >= 7 && token.startsWith("---")) {
            val grade = token.substring(3, token.lastIndex - 2)
            this.integrateConnection(grade, false)
        } else {
            this.integrateSymbol(token)
        }
    }

    private fun integrateSymbol(symbol: String) {
        val symbolToAdd = this.symbolMap.getOrCreateId(symbol)
        if (this.grade == -1) {
            this.left.add(symbolToAdd)
        } else {
            this.right.add(symbolToAdd)
        }
    }

    private fun integrateConnection(grade:Int, isBidirectional:Boolean) {
        if (this.grade != -1) {
            throw ParseAxiomException("Second connection found at axiom ${this.name}")
        }

        this.grade = grade
        this.isBidirectional = isBidirectional
    }

    private fun integrateConnection(grade:String, isBidirectional:Boolean) {
        try {
            val gradeAsInt = Integer.parseInt(grade)
            if (gradeAsInt < 0) {
                throw ParseAxiomException("Grade of axiom ${this.name} is negative. Maybe one '-' too much?")
            }
            this.integrateConnection(gradeAsInt, isBidirectional)
        } catch (e: NumberFormatException) {}
    }


    /// PARSING
    private fun isWhiteSpaceOrNewLine(ch:Char) : Boolean {
        return ch == ' ' ||
                ch == '\t' ||
                ch == '\r' ||
                ch == '\n'
    }

    private fun readNextNonWhiteNorNewLine() : Char {
        while (true) {
            val nextChar = this.getNextChar()
            if (!this.isWhiteSpaceOrNewLine(nextChar)) {
                return nextChar
            }
        }
    }

    private fun getNextChar() : Char {
        if (this.rolledBackCharacter != null) {
            val ret = this.rolledBackCharacter!!
            this.rolledBackCharacter = null
            return ret
        }

        val nextByte = this.reader.read()
        return if (nextByte == -1)
            '\u0000' else
            nextByte.toChar()
    }

    private fun getNextToken(firstChar:Char) : String {
        val builder = StringBuilder().append(firstChar)
        while(true) {
            val nextChar = this.getNextChar()
            if(nextChar == '\u0000' ||this.isWhiteSpaceOrNewLine(nextChar)) {
                break
            } else if (nextChar == '"') {
                this.rolledBackCharacter = nextChar
                break
            }

            builder.append(nextChar)
        }

        return builder.toString()
    }
}

private class ParseAxiomException(message:String) : ParserException(message)



