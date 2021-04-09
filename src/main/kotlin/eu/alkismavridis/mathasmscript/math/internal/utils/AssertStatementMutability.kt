package eu.alkismavridis.mathasmscript.math.internal.utils

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException


@Throws(StatementImmutableException::class)
fun assertStatementMutability(type: StatementType) {
    if (type.canChange) return
    throw StatementImmutableException("Statement of type ${type.name} is immutable")
}

class StatementImmutableException(message:String) : MathAsmException(message)
