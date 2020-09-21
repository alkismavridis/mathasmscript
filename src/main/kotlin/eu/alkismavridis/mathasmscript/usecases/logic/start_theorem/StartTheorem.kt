package eu.alkismavridis.mathasmscript.usecases.logic.start_theorem

import eu.alkismavridis.mathasmscript.model.logic.*
import eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality.assertBaseLegality

fun startTheorem(name:String, base: MathAsmStatement, side: StatementSide) : MathAsmStatement {
    assertStartValidity(base, side)

    val left:MathAsmSentence
    val right:MathAsmSentence
    when (side) {
        StatementSide.BOTH -> {
            left = MathAsmSentence(base.left)
            right = MathAsmSentence(base.right)
        }

        StatementSide.LEFT -> {
            left = MathAsmSentence(base.left)
            right = MathAsmSentence(base.left)
        }

        StatementSide.RIGHT -> {
            left = MathAsmSentence(base.right)
            right = MathAsmSentence(base.right)
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
