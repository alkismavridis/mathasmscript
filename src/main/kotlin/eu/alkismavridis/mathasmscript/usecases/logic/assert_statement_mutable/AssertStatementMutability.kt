package eu.alkismavridis.mathasmscript.usecases.logic.assert_statement_mutable

import eu.alkismavridis.mathasmscript.model.logic.StatementType


@Throws(StatementImmutableException::class)
fun assertStatementMutability(type: StatementType) {
    if (type.canChange) return
    throw StatementImmutableException("Statement of type ${type.name} is immutable")
}
