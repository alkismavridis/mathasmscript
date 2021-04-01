package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.MathAsmExpression
import eu.alkismavridis.mathasmscript.core.MathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementType

fun getOpenTheorem(target: MathAsmStatement, nameForClones:String) : MathAsmStatement {
    return if (target.type.canChange)
        target else
        MathAsmStatement(
            nameForClones,
            StatementType.OPEN_THEOREM,
            MathAsmExpression(target.left),
            MathAsmExpression(target.right),
            target.isBidirectional,
            target.grade
    )
}
