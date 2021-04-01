package eu.alkismavridis.mathasmscript.usecases.parser

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.parser.result.MasVariable
import eu.alkismavridis.mathasmscript.entities.parser.result.MasVariableKind

fun extractExportedValues(variables: Collection<MasVariable>) : List<FixedMasStatement> {
    return variables
            .asSequence()
            .filter { it.kind == MasVariableKind.EXPORT }
            .map { it.value }
            .toList()
}

fun extractImports(variables: Collection<MasVariable>) : List<MasVariable> {
    return variables.filter { it.kind == MasVariableKind.IMPORT }
}
