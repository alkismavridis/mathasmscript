package eu.alkismavridis.mathasmscript.parser.model

class ParserResult(
        val status: ParserResultStatus,
        val scriptName: String,
        val packageName: String,
        val variables: Collection<MasVariable>,
        val comments: Collection<InspectionComment>
)


