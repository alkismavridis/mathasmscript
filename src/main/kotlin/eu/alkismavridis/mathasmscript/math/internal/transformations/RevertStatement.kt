package eu.alkismavridis.mathasmscript.math.internal.transformations

import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement

fun handleStatementRevert(target: MutableMathAsmStatement) : MutableMathAsmStatement {
    if (!target.isBidirectional) {
        throw MathAsmException("Unidirectional base ${target.name} cannot be reverted")
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
