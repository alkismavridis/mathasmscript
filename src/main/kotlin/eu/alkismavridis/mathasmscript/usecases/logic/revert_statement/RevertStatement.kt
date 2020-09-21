package eu.alkismavridis.mathasmscript.usecases.logic.revert_statement

import eu.alkismavridis.mathasmscript.model.logic.MathAsmStatement

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
