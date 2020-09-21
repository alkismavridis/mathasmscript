package eu.alkismavridis.mathasmscript.model.parser

enum class InspectionType {
    ERROR, WARNING, INFO, DEBUG
}

class InspectionComment(
        val line:Int,
        val column:Int,
        val message:String,
        val type: InspectionType
)
