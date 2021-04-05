package eu.alkismavridis.mathasmscript.testhelpers

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.internal.MathAsmExpression

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
