package eu.alkismavridis.mathasmscript.math

fun interface SymbolMapper {
    fun toTextForm(symbolId: Long) : String
}

interface MathAsmStatement {
    val name : String
    val type: StatementType
    fun toStatementString(mapper: SymbolMapper) : String
}
