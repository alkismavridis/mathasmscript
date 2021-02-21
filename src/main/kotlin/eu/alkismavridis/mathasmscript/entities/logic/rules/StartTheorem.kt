package eu.alkismavridis.mathasmscript.usecases.logic.start_theorem

import eu.alkismavridis.mathasmscript.entities.logic.*
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.IllegalTheoremStartException
import eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality.assertBaseLegality

fun startTheorem(name:String, base: MathAsmStatement, side: StatementSide) : MathAsmStatement {
    assertStartValidity(base, side)

    val left:MathAsmExpression
    val right:MathAsmExpression
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

    return MathAsmStatement(
            name,
            StatementType.OPEN_THEOREM,
            left,
            right,
            base.isBidirectional,
            base.grade
    )
}

private fun assertStartValidity(base: MathAsmStatement, side: StatementSide) {
    assertBaseLegality(base.type)
    if (side == StatementSide.RIGHT && !base.isBidirectional) {
        throw IllegalTheoremStartException("Cannot clone right side of unidirectional base ${base.name}")
    }
}
