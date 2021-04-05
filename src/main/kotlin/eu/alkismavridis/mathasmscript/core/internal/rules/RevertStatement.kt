package eu.alkismavridis.mathasmscript.core.internal.rules

import eu.alkismavridis.mathasmscript.core.internal.IllegalDirectionException
import eu.alkismavridis.mathasmscript.core.internal.MutableMathAsmStatement

fun revertStatement(target: MutableMathAsmStatement) : MutableMathAsmStatement {
    if (!target.isBidirectional) {
        throw IllegalDirectionException("Unidirectional base ${target.name} cannot be reverted")
    }

    return MutableMathAsmStatement(
            target.name,
            target.type,
            target.right,
            target.left,
            target.isBidirectional,
            target.grade
    )
}
