package eu.alkismavridis.mathasmscript.entities.parser

import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement

class ParseResult(
        val success: Boolean,
        val packageName: String,
        val scriptName: String,
        val exportedStatements: Collection<FixedMasStatement>,
        val comments: Collection<InspectionComment>
)
