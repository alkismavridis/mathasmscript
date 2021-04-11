package eu.alkismavridis.mathasmscript.parser.internal.token

open class MasToken(val type: MasTokenType, val line: Int, val column: Int) {
    open fun getTextRepresentation(): String {
        return this.type.textRepresentation
    }
}

class NameToken(val name: String, line: Int, column: Int) : MasToken(MasTokenType.NAME, line, column) {
    override fun getTextRepresentation(): String {
        return this.name
    }
}

class NumberToken(val value: Int, line: Int, column: Int) : MasToken(MasTokenType.NUMBER, line, column) {
    override fun getTextRepresentation(): String {
        return "${this.value}"
    }
}

class StringToken(val text: String, line: Int, column: Int) : MasToken(MasTokenType.STRING, line, column) {
    override fun getTextRepresentation(): String {
        return if (this.text.length < 15)
            "\"${this.text}\"" else
            "\"${this.text.substring(0, 13)}...\""
    }
}
