package eu.alkismavridis.mathasmscript.old.usecases.parser

import eu.alkismavridis.mathasmscript.core.FixedMasStatement
import eu.alkismavridis.mathasmscript.parser.result.MasVariable
import eu.alkismavridis.mathasmscript.parser.result.MasVariableKind

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
