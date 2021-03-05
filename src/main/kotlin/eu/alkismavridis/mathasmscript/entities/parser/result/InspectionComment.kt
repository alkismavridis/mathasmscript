package eu.alkismavridis.mathasmscript.entities.parser.result

class InspectionComment(
        val line:Int,
        val column:Int,
        val message:String,
        val type: InspectionType
)
