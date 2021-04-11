package eu.alkismavridis.mathasmscript.parser

import eu.alkismavridis.mathasmscript.parser.result.MasVariable
import eu.alkismavridis.mathasmscript.parser.result.MasVariableKind
import eu.alkismavridis.mathasmscript.theory.model.FixedMasStatement
import eu.alkismavridis.mathasmscript.theory.model.MasScriptImport

fun extractExportedValues(variables: Collection<MasVariable>) : List<FixedMasStatement> {
    return variables
            .asSequence()
            .filter { it.kind == MasVariableKind.EXPORT }
            .map { it.value }
            .toList()
}

fun extractImports(variables: Collection<MasVariable>) : List<MasScriptImport> {
    return variables
            .asSequence()
            .filter { it.kind == MasVariableKind.IMPORT }
            .map { MasScriptImport(
                it.value.id,
                if (it.value.path.isEmpty()) "" else "${it.value.path} -> ${it.importUrl}"
            )
            }
            .toList()
}
