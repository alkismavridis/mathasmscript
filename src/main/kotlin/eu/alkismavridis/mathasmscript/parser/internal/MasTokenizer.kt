package eu.alkismavridis.mathasmscript.parser.internal

import java.io.Reader


class MasTokenizer(private val reader: Reader, private val logger: MathasmInspections) {
    private var line = 1
    private var column = 1
    private var rolledBackCharacter: Char? = null


    @Throws(TokenizerException::class)
    fun nextToken(): MasToken {
        val currentColumn = this.column
        val currentLine = this.line
        val nextChar = this.readNextNonWhite()

        when (nextChar) {
            '(' -> return MasToken(MasTokenType.PARENTHESIS_OPEN, currentLine, currentColumn)
            ')' -> return MasToken(MasTokenType.PARENTHESIS_CLOSE, currentLine, currentColumn)
            '{' -> return MasToken(MasTokenType.CURLY_OPEN, currentLine, currentColumn)
            '}' -> return MasToken(MasTokenType.CURLY_CLOSE, currentLine, currentColumn)
            '=' -> return MasToken(MasTokenType.EQUALS, currentLine, currentColumn)
            '.' -> return MasToken(MasTokenType.DOT, currentLine, currentColumn)
            ',' -> return MasToken(MasTokenType.COMMA, currentLine, currentColumn)
            ':' -> return MasToken(MasTokenType.COLON, currentLine, currentColumn)
            '\n' -> return MasToken(MasTokenType.NEW_LINE, currentLine, currentColumn)
            '/' -> {
                this.skipComment()
                return this.nextToken()
            }

            '"' -> {
                return this.parseString()
            }
            '\u0000' -> return MasToken(MasTokenType.EOF, currentLine, this.column)
        }

        if (this.isLetter(nextChar)) {
            this.rollBackCharacter(nextChar)
            return this.getNextNamedToken()
        } else if (this.isDigit(nextChar)) {
            this.rollBackCharacter(nextChar)
            return this.getNextNumberToken()
        }

        throw this.addError(this.line, this.column, "Unexpected character: $nextChar")
    }

    private fun parseString(): StringToken {
        val builder = StringBuilder()
        val startingLine = this.line
        val startingColumn = this.column - 1

        while (true) {
            val next = this.getNextChar()
            when (next) {
                '"' -> return StringToken(builder.toString(), startingLine, startingColumn)
                '\u0000' -> throw this.addError(this.line, this.column, "End of file found while parsing string")
                else -> builder.append(next)
            }
        }
    }

    private fun getNextNamedToken(): NameToken {
        val builder = StringBuilder()
        val startingColumn = this.column
        val startingLine = this.line

        while (true) {
            val nextChar = this.getNextChar()
            if (this.isLetterOrDigit(nextChar)) {
                builder.append(nextChar)
            } else {
                this.rollBackCharacter(nextChar)
                break
            }
        }

        return NameToken(builder.toString(), startingLine, startingColumn)
    }

    private fun getNextNumberToken(): NumberToken {
        val builder = StringBuilder()
        val startingColumn = this.column
        val startingLine = this.line

        while (true) {
            val nextChar = this.getNextChar()
            if (this.isDigit(nextChar)) {
                builder.append(nextChar)
            } else {
                this.rollBackCharacter(nextChar)
                break
            }
        }

        val valueString = builder.toString()
        try {
            return NumberToken(Integer.parseInt(valueString), startingLine, startingColumn)
        } catch (e: NumberFormatException) {
            throw this.addError(startingLine, startingColumn, "Invalid number: $valueString")
        }
    }

    private fun skipComment() {
        val typeOfComment = this.getNextChar()
        if (typeOfComment == '/') {
            // skip until end of line
            while (true) {
                val nextChar = this.getNextChar()
                if (nextChar == '\u0000' || nextChar == '\n') {
                    return
                }
            }
        } else if (typeOfComment == '*') {
            // skip multiline comment.
            while (true) {
                val nextChar = this.getNextChar()
                if (nextChar == '\u0000') {
                    throw this.addError(this.line, this.column,"EOF found while parsing multiline comment")
                } else if (nextChar == '*') {
                    val afterNext = this.getNextChar()
                    if (afterNext == '/') return
                    else {
                        this.rollBackCharacter(afterNext)
                    }
                }
            }
        }
    }


    /// CHAR IDENTIFICATION
    private fun isLetter(ch: Char): Boolean {
        if (ch in 'a'..'z') return true
        if (ch in 'A'..'Z') return true
        if (ch == '_') return true
        return false
    }

    private fun isLetterOrDigit(ch: Char): Boolean {
        if (ch in 'a'..'z') return true
        if (ch in 'A'..'Z') return true
        if (ch == '_') return true
        if (ch in '0'..'9') return true
        return false
    }

    private fun isDigit(ch: Char): Boolean {
        return ch in '0'..'9'
    }

    private fun isWhiteSpace(ch: Char): Boolean {
        return ch == ' ' ||
                ch == '\t' ||
                ch == '\r'
    }


    /// CHARACTER READING
    private fun readNextNonWhite(): Char {
        while (true) {
            val nextChar = this.getNextChar()
            if (!this.isWhiteSpace(nextChar)) {
                return nextChar
            }
        }
    }

    private fun getNextChar(): Char {
        if (this.rolledBackCharacter != null) {
            val ret = this.rolledBackCharacter!!
            this.rolledBackCharacter = null

            this.column++ // TODO this will not work if adjusted character was a new line
            return ret
        }

        val nextByte = this.reader.read()
        if (nextByte == -1) return '\u0000'

        val asChar = nextByte.toChar()
        if (asChar == '\n') {
            this.line++
            this.column = 1
        } else {
            this.column++
        }

        return asChar
    }

    fun rollBackCharacter(ch:Char) {
        this.rolledBackCharacter = ch
        this.column--

        // TODO adjusting column simply like that will not work for \n rollbacks
    }


    private fun addError(line:Int, column:Int, message:String) : TokenizerException {
        this.logger.error(line, column, message)
        return TokenizerException("Line ${line}_${column}: $message")
    }
}

class TokenizerException(message: String) : ParserException(message)
