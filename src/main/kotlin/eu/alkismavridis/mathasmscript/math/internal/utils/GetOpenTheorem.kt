package eu.alkismavridis.mathasmscript.math.internal.utils

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmExpression
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement

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
