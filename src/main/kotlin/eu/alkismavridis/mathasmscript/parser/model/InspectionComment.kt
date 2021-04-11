package eu.alkismavridis.mathasmscript.parser.model

class InspectionComment(
        val line:Int,
        val column:Int,
        val message:String,
        val type: InspectionType
)
