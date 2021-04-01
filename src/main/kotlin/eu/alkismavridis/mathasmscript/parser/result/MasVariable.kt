package eu.alkismavridis.mathasmscript.parser.result

import eu.alkismavridis.mathasmscript.repo.FixedMasStatement

class MasVariable(
        val kind: MasVariableKind,
        val name: String,
        val value: FixedMasStatement,
        val importUrl: String
)
