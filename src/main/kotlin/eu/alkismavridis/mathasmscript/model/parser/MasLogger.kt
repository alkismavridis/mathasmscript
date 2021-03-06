package eu.alkismavridis.mathasmscript.model.parser

import eu.alkismavridis.mathasmscript.model.logic.MathAsmException

class MasLogger {
    private val comments = mutableListOf<InspectionComment>()


    fun error(message:String) {
        this.comments.add(InspectionComment(-1, -1, message, InspectionType.ERROR))
    }

    fun error(line: Int, column: Int, message: String) {
        this.comments.add(InspectionComment(line, column, message, InspectionType.ERROR))
    }

    fun warn(line: Int, column: Int, message: String) {
        this.comments.add(InspectionComment(line, column, message, InspectionType.WARNING))
    }

    fun assertNoErrors() {
        if (!this.hasErrors()) return
        throw MathAsmException("Fatal errors were detected. Abort.")
    }

    fun getComments() : List<InspectionComment> {
        return this.comments
    }

    fun hasErrors(): Boolean {
        return this.comments.find { it.type == InspectionType.ERROR } != null
    }
}
