package eu.alkismavridis.mathasmscript.entities.logic.rules

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.logic.MathAsmStatement
import eu.alkismavridis.mathasmscript.entities.logic.StatementType

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
