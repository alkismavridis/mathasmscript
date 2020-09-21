package eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality

import eu.alkismavridis.mathasmscript.model.logic.StatementType


@Throws(IllegalBaseException::class)
fun assertBaseLegality(type: StatementType) {
    if (type.canUseAsBase) return
    throw IllegalBaseException("Statement of type ${type.name} cannot be used as a base")
}
