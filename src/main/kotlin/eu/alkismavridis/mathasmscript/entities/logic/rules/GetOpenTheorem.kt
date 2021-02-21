package eu.alkismavridis.mathasmscript.usecases.logic.get_open_theorem

import eu.alkismavridis.mathasmscript.entities.logic.MathAsmExpression
import eu.alkismavridis.mathasmscript.entities.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.entities.logic.StatementType

fun getOpenTheorem(target:MathAsmStatement, nameForClones:String) : MathAsmStatement {
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
