package eu.alkismavridis.mathasmscript.testhelpers

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmExpression
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement

class Fixtures {
    companion object {
        fun createBase(isBidirectional: Boolean, grade:Int, type: StatementType) = MutableMathAsmStatement(
                "someBase",
                type,
                MathAsmExpression(longArrayOf(1,4), false),
                MathAsmExpression(longArrayOf(9,3,9), false),
                isBidirectional,
                grade
        )

        fun createReverseBase(isBidirectional: Boolean, grade:Int, type: StatementType) = MutableMathAsmStatement(
                "someReversedBase",
                type,
                MathAsmExpression(longArrayOf(9,3,9), false),
                MathAsmExpression(longArrayOf(1,4), false),
                isBidirectional,
                grade
        )

        fun createTarget(isBidirectional: Boolean, grade:Int, type: StatementType) = MutableMathAsmStatement(
                "someTarget",
                type,
                MathAsmExpression(longArrayOf(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6), false),
                MathAsmExpression(longArrayOf(1, 1, 4, 9, 3, 9, 1, 4), false),
                isBidirectional,
                grade
        )
    }
}
