package eu.alkismavridis.mathasmscript.parser.converters

import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.parser.parse_script.MasParserException
import eu.alkismavridis.mathasmscript.repo.FixedStatementType

fun StatementType.toFixedStatementType() : FixedStatementType {
    return when(this) {
        StatementType.AXIOM -> FixedStatementType.AXIOM
        StatementType.THEOREM -> FixedStatementType.THEOREM
        else -> throw MasParserException("Cannot map StatementType ${this.name} to FixedStatementType")
    }
}

fun FixedStatementType.toStatementType() : StatementType {
    return when(this) {
        FixedStatementType.AXIOM -> StatementType.AXIOM
        FixedStatementType.THEOREM -> StatementType.THEOREM
    }
}
