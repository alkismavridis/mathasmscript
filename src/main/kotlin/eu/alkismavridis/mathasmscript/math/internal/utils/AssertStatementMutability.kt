package eu.alkismavridis.mathasmscript.math.internal.utils

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException


@Throws(MathAsmException::class)
fun assertStatementMutability(type: StatementType) {
    if (type.canChange) return
    throw MathAsmException("Statement of type ${type.name} is immutable")
}
