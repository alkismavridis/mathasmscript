package eu.alkismavridis.mathasmscript.model.parser

import eu.alkismavridis.mathasmscript.model.repo.FixedMasStatement

class ParseResult(
        val success: Boolean,
        val packageName: String,
        val scriptName: String,
        val exportedStatements: Collection<FixedMasStatement>,
        val comments: Collection<InspectionComment>
)
