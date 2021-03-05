package eu.alkismavridis.mathasmscript.entities.parser.result

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement

class ParseResult(
        val success: Boolean,
        val scriptName: String,
        val packageName: String,
        val imports: Collection<MasImport>,
        val exports: Collection<FixedMasStatement>,
        val comments: Collection<InspectionComment>
)


