package eu.alkismavridis.mathasmscript.model.parser

enum class MasTokenType(val textRepresentation:String) {
    NAME("name"),
    NUMBER("number"),
    EQUALS("="),
    STRING("string"),
    PARENTHESIS_OPEN("("),
    PARENTHESIS_CLOSE(")"),
    CURLY_OPEN("{"),
    CURLY_CLOSE("}"),
    NEW_LINE("\\n"),
    DOT("."),
    COMMA(","),
    COLON(":"),
    EOF("EOF")
}
