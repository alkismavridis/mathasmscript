package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.internal.MathAsmException

class StabilizeTheoremException(message:String) : MathAsmException(message)


fun stabilizeOpenTheorem(openTheorem: MutableMathAsmStatement) : MutableMathAsmStatement {
    if (openTheorem.type != StatementType.OPEN_THEOREM) {
        throw StabilizeTheoremException("Only statements of type OPEN_THEOREM can become theorems")
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
