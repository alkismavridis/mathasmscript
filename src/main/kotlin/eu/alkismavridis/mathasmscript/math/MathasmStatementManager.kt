package eu.alkismavridis.mathasmscript.math

import eu.alkismavridis.mathasmscript.math.internal.model.LogicSelection
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmExpression
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.math.internal.transformations.*

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
        return handleTheoremStabilization(this.assertMutableStatement(stmt))
    }

    fun replaceAll(target: MathAsmStatement, base: MathAsmStatement, nameForClones:String) : MathAsmStatement {
        return handleReplaceAll(
                this.assertMutableStatement(target),
                this.assertMutableStatement(base),
                this.selection,
                nameForClones
        )
    }

    fun startTheorem(name: String, target: MathAsmStatement, side: StatementSide) : MathAsmStatement {
        return handleTheoremStart(name, this.assertMutableStatement(target), side)
    }

    fun replaceAllInSentence(target: MathAsmStatement, side: StatementSide, base: MathAsmStatement, name: String): MathAsmStatement {
        return replaceAllInSentence(
                this.assertMutableStatement(target),
                side,
                this.assertMutableStatement(base),
                this.selection,
                name
        )
    }

    fun replaceSingleMatch(target: MathAsmStatement, side: StatementSide, position: Int, base: MathAsmStatement, name: String): MathAsmStatement {
        return handleSingleMatchReplacement(
                this.assertMutableStatement(target),
                side,
                position,
                this.assertMutableStatement(base),
                this.selection,
                name
        )
    }

    fun revertStatement(stmt: MathAsmStatement): MathAsmStatement {
        return handleStatementRevert(this.assertMutableStatement(stmt))
    }

    private fun assertMutableStatement(stmt: MathAsmStatement) : MutableMathAsmStatement {
        return stmt as? MutableMathAsmStatement
                ?: throw IllegalArgumentException("Statement ${stmt.name} is not mutable")
    }
}
