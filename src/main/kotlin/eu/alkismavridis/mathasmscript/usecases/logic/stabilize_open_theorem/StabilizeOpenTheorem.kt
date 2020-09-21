package eu.alkismavridis.mathasmscript.usecases.logic.stabilize_open_theorem

import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.model.logic.StatementType

class StabilizeTheoremException(message:String) : MathAsmException(message)


fun stabilizeOpenTheorem(openTheorem:MathAsmStatement) : MathAsmStatement {
    if (openTheorem.type != StatementType.OPEN_THEOREM) {
        throw StabilizeTheoremException("Only statements of type OPEN_THEOREM can become theorems")
    }

    val result = MathAsmStatement(
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
