package eu.alkismavridis.mathasmscript.entities.parser.result

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement

class MasVariable(
        val kind: MasVariableKind,
        val name: String,
        val value: FixedMasStatement,
        val importUrl: String
)
