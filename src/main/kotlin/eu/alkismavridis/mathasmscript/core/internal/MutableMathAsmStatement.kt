package eu.alkismavridis.mathasmscript.core.internal

import eu.alkismavridis.mathasmscript.core.MathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.SymbolMapper

class MutableMathAsmStatement(
        override val name:String,
        override val type: StatementType,
        val left: MathAsmExpression,
        val right: MathAsmExpression,
        val isBidirectional: Boolean,
        val grade: Int = 0
) : MathAsmStatement {
    fun saveSpace() {
        this.left.saveSpace()
        this.right.saveSpace()
    }

    override fun toStatementString(mapper: SymbolMapper) : String {
        val builder = StringBuilder()
        for (i in 0 until this.left.length) {
            builder.append( mapper.toTextForm(this.left.words[i]) ).append(" ")
        }

        if (this.isBidirectional) {
            builder.append( if(this.grade == 0) "<--->" else "<--${this.grade}-->" ).append(" ")
        } else {
            builder.append( if(this.grade == 0) "--->" else "---${this.grade}-->" ).append(" ")
        }

        for (i in 0 until this.right.length) {
            builder.append( mapper.toTextForm(this.right.words[i]) ).append(" ")
        }

        return builder.toString()
    }
}
