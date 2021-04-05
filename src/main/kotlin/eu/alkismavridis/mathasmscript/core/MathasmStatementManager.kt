package eu.alkismavridis.mathasmscript.core

import eu.alkismavridis.mathasmscript.core.internal.LogicSelection
import eu.alkismavridis.mathasmscript.core.internal.MathAsmExpression
import eu.alkismavridis.mathasmscript.core.internal.MutableMathAsmStatement

class MathasmStatementManager {
    private val selection = LogicSelection(100)


    fun createStatement(name: String, type: StatementType, left: LongArray, right: LongArray, isBidirectional: Boolean, grade: Int) : MathAsmStatement {
        return MutableMathAsmStatement(
                name,
                type,
                MathAsmExpression(left, false),
                MathAsmExpression(right, false),
                isBidirectional,
                grade
        )
    }

    fun stabilizeOpenTheorem(stmt: MathAsmStatement) : MathAsmStatement {
        return eu.alkismavridis.mathasmscript.core.internal.rules.stabilizeOpenTheorem(this.assertMutableStatement(stmt))
    }

    fun replaceAll(target: MathAsmStatement, base: MathAsmStatement, nameForClones:String) : MathAsmStatement {
        return eu.alkismavridis.mathasmscript.core.internal.rules.replaceAll(
                this.assertMutableStatement(target),
                this.assertMutableStatement(base),
                this.selection,
                nameForClones
        )
    }

    fun startTheorem(name: String, target: MathAsmStatement, side: StatementSide) : MathAsmStatement {
        return eu.alkismavridis.mathasmscript.core.internal.rules.startTheorem(name, this.assertMutableStatement(target), side)
    }


    private fun assertMutableStatement(stmt: MathAsmStatement) : MutableMathAsmStatement {
        return stmt as? MutableMathAsmStatement
                ?: throw IllegalArgumentException("Statement ${stmt.name} is not mutable")
    }

    fun replaceAllInSentence(target: MathAsmStatement, side: StatementSide, base: MathAsmStatement, name: String): MathAsmStatement {
        return eu.alkismavridis.mathasmscript.core.internal.rules.replaceAllInSentence(
                this.assertMutableStatement(target),
                side,
                this.assertMutableStatement(base),
                this.selection,
                name
        )
    }

    fun replaceSingleMatch(target: MathAsmStatement, side: StatementSide, position: Int, base: MathAsmStatement, name: String): MathAsmStatement {
        return eu.alkismavridis.mathasmscript.core.internal.rules.replaceSingleMatch(
                this.assertMutableStatement(target),
                side,
                position,
                this.assertMutableStatement(base),
                this.selection,
                name
        )
    }

    fun revertStatement(stmt: MathAsmStatement): MathAsmStatement {
        return eu.alkismavridis.mathasmscript.core.internal.rules.revertStatement(this.assertMutableStatement(stmt))
    }
}
