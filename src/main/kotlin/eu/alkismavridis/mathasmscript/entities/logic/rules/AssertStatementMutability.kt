package eu.alkismavridis.mathasmscript.usecases.logic.assert_statement_mutable

import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.StatementImmutableException


@Throws(StatementImmutableException::class)
fun assertStatementMutability(type: StatementType) {
    if (type.canChange) return
    throw StatementImmutableException("Statement of type ${type.name} is immutable")
}
