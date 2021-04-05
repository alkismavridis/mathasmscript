package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.core.internal.IllegalDirectionException

fun revertStatement(target: MutableMathAsmStatement) : MutableMathAsmStatement {
    if (!target.isBidirectional) {
        throw IllegalDirectionException("Unidirectional base ${target.name} cannot be reverted")
    }

    if (target.type.canChange) {
        target.reverse()
        return target
    } else {
        //make a shallow copy
        return MutableMathAsmStatement(
                target.name,
                target.type,
                target.right,
                target.left,
                target.isBidirectional,
                target.grade
        )
    }
}
