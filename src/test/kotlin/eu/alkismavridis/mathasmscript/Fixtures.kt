package eu.alkismavridis.mathasmscript

import eu.alkismavridis.mathasmscript.model.logic.MathAsmSentence
import eu.alkismavridis.mathasmscript.model.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.model.logic.StatementType

class Fixtures {
    companion object {
        fun createBase(isBidirectional: Boolean, grade:Int, type: StatementType) = MathAsmStatement(
                "someBase",
                type,
                MathAsmSentence(longArrayOf(1,4), false),
                MathAsmSentence(longArrayOf(9,3,9), false),
                isBidirectional,
                grade
        )

        fun createReverseBase(isBidirectional: Boolean, grade:Int, type: StatementType) = MathAsmStatement(
                "someReversedBase",
                type,
                MathAsmSentence(longArrayOf(9,3,9), false),
                MathAsmSentence(longArrayOf(1,4), false),
                isBidirectional,
                grade
        )

        fun createTarget(isBidirectional: Boolean, grade:Int, type: StatementType) = MathAsmStatement(
                "someTarget",
                type,
                MathAsmSentence(longArrayOf(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6), false),
                MathAsmSentence(longArrayOf(1, 1, 4, 9, 3, 9, 1, 4), false),
                isBidirectional,
                grade
        )
    }
}
