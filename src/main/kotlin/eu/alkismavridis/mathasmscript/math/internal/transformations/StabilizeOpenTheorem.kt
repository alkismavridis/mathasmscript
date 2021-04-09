package eu.alkismavridis.mathasmscript.math.internal.transformations

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement

fun handleTheoremStabilization(openTheorem: MutableMathAsmStatement) : MutableMathAsmStatement {
    if (openTheorem.type != StatementType.OPEN_THEOREM) {
        throw MathAsmException("Only statements of type OPEN_THEOREM can become theorems")
    }

    val result = MutableMathAsmStatement(
            openTheorem.name,
            StatementType.THEOREM,
            openTheorem.left,
            openTheorem.right,
            openTheorem.isBidirectional,
            openTheorem.grade
    )
    result.saveSpace()

    return result
}
