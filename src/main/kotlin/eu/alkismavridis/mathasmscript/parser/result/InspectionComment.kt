package eu.alkismavridis.mathasmscript.parser.result

class InspectionComment(
        val line:Int,
        val column:Int,
        val message:String,
        val type: InspectionType
)
