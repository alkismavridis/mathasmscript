package eu.alkismavridis.mathasmscript.parser.model

import eu.alkismavridis.mathasmscript.theory.model.FixedMasStatement

class MasVariable(
        val kind: MasVariableKind,
        val name: String,
        val value: FixedMasStatement,
        val importUrl: String
)
