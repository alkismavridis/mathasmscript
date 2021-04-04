package eu.alkismavridis.mathasmscript.core.internal

import eu.alkismavridis.mathasmscript.core.StatementType


@Throws(StatementImmutableException::class)
fun assertStatementMutability(type: StatementType) {
    if (type.canChange) return
    throw StatementImmutableException("Statement of type ${type.name} is immutable")
}
