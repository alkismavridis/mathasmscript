package eu.alkismavridis.mathasmscript.entities.parser.result

class ParserResult(
        val status: ParserResultStatus,
        val scriptName: String,
        val packageName: String,
        val variables: Collection<MasVariable>,
        val comments: Collection<InspectionComment>
)

enum class ParserResultStatus {
    ERROR,
    EXECUTED,
    IMPORTED
}
