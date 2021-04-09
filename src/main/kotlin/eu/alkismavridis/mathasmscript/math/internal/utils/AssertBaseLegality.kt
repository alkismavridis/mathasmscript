package eu.alkismavridis.mathasmscript.math.internal.utils

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException

@Throws(MathAsmException::class)
fun assertBaseLegality(type: StatementType) {
    if (type.canUseAsBase) return
    throw MathAsmException("Statement of type ${type.name} cannot be used as a base")
}
