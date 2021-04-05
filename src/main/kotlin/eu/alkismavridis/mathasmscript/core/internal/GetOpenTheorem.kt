package eu.alkismavridis.mathasmscript.core.internal

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementType

fun getOpenTheorem(target: MutableMathAsmStatement, nameForClones:String) : MutableMathAsmStatement {
    return if (target.type.canChange)
        target else
        MutableMathAsmStatement(
            nameForClones,
            StatementType.OPEN_THEOREM,
            MathAsmExpression(target.left),
            MathAsmExpression(target.right),
            target.isBidirectional,
            target.grade
    )
}
