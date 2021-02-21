package eu.alkismavridis.mathasmscript.entities.logic.rules

import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.StatementImmutableException


@Throws(StatementImmutableException::class)
fun assertStatementMutability(type: StatementType) {
    if (type.canChange) return
    throw StatementImmutableException("Statement of type ${type.name} is immutable")
}
