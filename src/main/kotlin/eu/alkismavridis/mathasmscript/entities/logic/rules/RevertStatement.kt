package eu.alkismavridis.mathasmscript.entities.logic.rules

import eu.alkismavridis.mathasmscript.entities.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.IllegalDirectionException

fun revertStatement(target: MathAsmStatement) : MathAsmStatement {
    if (!target.isBidirectional) {
        throw IllegalDirectionException("Unidirectional base ${target.name} cannot be reverted")
    }

    if (target.type.canChange) {
        target.reverse()
        return target
    } else {
        //make a shallow copy
        return MathAsmStatement(
                target.name,
                target.type,
                target.right,
                target.left,
                target.isBidirectional,
                target.grade
        )
    }
}
