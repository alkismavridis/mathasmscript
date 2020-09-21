package eu.alkismavridis.mathasmscript.usecases.logic.get_open_theorem

import eu.alkismavridis.mathasmscript.model.logic.MathAsmSentence
import eu.alkismavridis.mathasmscript.model.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.model.logic.StatementType

fun getOpenTheorem(target:MathAsmStatement, nameForClones:String) : MathAsmStatement {
    return if (target.type.canChange)
        target else
        MathAsmStatement(
            nameForClones,
            StatementType.OPEN_THEOREM,
            MathAsmSentence(target.left),
            MathAsmSentence(target.right),
            target.isBidirectional,
            target.grade
    )
}
