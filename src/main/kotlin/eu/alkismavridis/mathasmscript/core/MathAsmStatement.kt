package eu.alkismavridis.mathasmscript.core

fun interface SymbolMapper {
    fun toTextForm(symbolId: Long) : String
}

interface MathAsmStatement {
    val name : String
    val type: StatementType
    fun toStatementString(mapper: SymbolMapper) : String
}
