package eu.alkismavridis.mathasmscript.testhelpers

import eu.alkismavridis.mathasmscript.entities.logic.MathAsmExpression
import eu.alkismavridis.mathasmscript.entities.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.entities.logic.StatementType

class Fixtures {
    companion object {
        fun createBase(isBidirectional: Boolean, grade:Int, type: StatementType) = MathAsmStatement(
                "someBase",
                type,
                MathAsmExpression(longArrayOf(1,4), false),
                MathAsmExpression(longArrayOf(9,3,9), false),
                isBidirectional,
                grade
        )

        fun createReverseBase(isBidirectional: Boolean, grade:Int, type: StatementType) = MathAsmStatement(
                "someReversedBase",
                type,
                MathAsmExpression(longArrayOf(9,3,9), false),
                MathAsmExpression(longArrayOf(1,4), false),
                isBidirectional,
                grade
        )

        fun createTarget(isBidirectional: Boolean, grade:Int, type: StatementType) = MathAsmStatement(
                "someTarget",
                type,
                MathAsmExpression(longArrayOf(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6), false),
                MathAsmExpression(longArrayOf(1, 1, 4, 9, 3, 9, 1, 4), false),
                isBidirectional,
                grade
        )
    }
}
