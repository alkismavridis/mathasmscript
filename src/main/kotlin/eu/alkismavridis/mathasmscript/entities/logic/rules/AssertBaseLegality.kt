package eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality

import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.IllegalBaseException


@Throws(IllegalBaseException::class)
fun assertBaseLegality(type: StatementType) {
    if (type.canUseAsBase) return
    throw IllegalBaseException("Statement of type ${type.name} cannot be used as a base")
}
