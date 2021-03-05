package eu.alkismavridis.mathasmscript.entities.parser.result

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement

class MasImport(
        val internalName: String,
        val importUrl: String,
        val statement: FixedMasStatement
)
