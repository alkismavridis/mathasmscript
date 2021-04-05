package eu.alkismavridis.mathasmscript.core

import eu.alkismavridis.mathasmscript.core.internal.MathAsmExpression

class MutableMathAsmStatement(
        override val name:String,
        override val type: StatementType,
        left: MathAsmExpression,
        right: MathAsmExpression,
        val isBidirectional: Boolean,
        val grade: Int = 0
) : MathAsmStatement {
    var left: MathAsmExpression = left; private set
    var right: MathAsmExpression = right; private set


    /// MODIFIERS
    fun saveSpace() {
        this.left.saveSpace()
        this.right.saveSpace()
    }

    fun reverse() {
        val tmp = this.left
        this.left = this.right
        this.right = tmp
    }


    /// SERIALIZATION
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
