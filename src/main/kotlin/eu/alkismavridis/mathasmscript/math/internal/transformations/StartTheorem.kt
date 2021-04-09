package eu.alkismavridis.mathasmscript.math.internal.transformations

import eu.alkismavridis.mathasmscript.math.StatementSide
import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmExpression
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.math.internal.utils.assertBaseLegality

fun handleTheoremStart(name:String, base: MutableMathAsmStatement, side: StatementSide) : MutableMathAsmStatement {
    assertStartValidity(base, side)

    val left: MathAsmExpression
    val right: MathAsmExpression
    when (side) {
        StatementSide.BOTH -> {
            left = MathAsmExpression(base.left)
            right = MathAsmExpression(base.right)
        }

        StatementSide.LEFT -> {
            left = MathAsmExpression(base.left)
            right = MathAsmExpression(base.left)
        }

        StatementSide.RIGHT -> {
            left = MathAsmExpression(base.right)
            right = MathAsmExpression(base.right)
        }
    }

    return MutableMathAsmStatement(
            name,
            StatementType.OPEN_THEOREM,
            left,
            right,
            base.isBidirectional,
            base.grade
    )
}

private fun assertStartValidity(base: MutableMathAsmStatement, side: StatementSide) {
    assertBaseLegality(base.type)
    if (side == StatementSide.RIGHT && !base.isBidirectional) {
        throw MathAsmException("Cannot clone right side of unidirectional base ${base.name}")
    }
}
