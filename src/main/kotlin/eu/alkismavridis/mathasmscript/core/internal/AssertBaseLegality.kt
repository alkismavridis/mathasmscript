package eu.alkismavridis.mathasmscript.core.internal

import eu.alkismavridis.mathasmscript.core.StatementType


@Throws(IllegalBaseException::class)
fun assertBaseLegality(type: StatementType) {
    if (type.canUseAsBase) return
    throw IllegalBaseException("Statement of type ${type.name} cannot be used as a base")
}
