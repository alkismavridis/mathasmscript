package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.internal.MathAsmExpression

fun createAxiom(name: String, type: StatementType, left: LongArray, right: LongArray, isBidirectional: Boolean, grade: Int): MutableMathAsmStatement {
    return MutableMathAsmStatement(
            name,
            type,
            MathAsmExpression(left, false),
            MathAsmExpression(right, false),
            isBidirectional,
            grade
    )
}
