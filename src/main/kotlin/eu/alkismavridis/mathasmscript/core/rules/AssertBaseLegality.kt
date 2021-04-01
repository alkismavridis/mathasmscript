package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.exceptions.IllegalBaseException


@Throws(IllegalBaseException::class)
fun assertBaseLegality(type: StatementType) {
    if (type.canUseAsBase) return
    throw IllegalBaseException("Statement of type ${type.name} cannot be used as a base")
}
