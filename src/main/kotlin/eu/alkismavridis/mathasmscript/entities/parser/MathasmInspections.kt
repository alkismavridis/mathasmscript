package eu.alkismavridis.mathasmscript.entities.parser

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.parser.result.InspectionComment
import eu.alkismavridis.mathasmscript.entities.parser.result.InspectionType

class MathasmInspections {
    private val comments = mutableListOf<InspectionComment>()

    fun appError(message:String) {
        this.comments.add(InspectionComment(-1, -1, message, InspectionType.APP_ERROR))
    }

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

    fun getEntries() : List<InspectionComment> {
        return this.comments
    }

    fun hasErrors(): Boolean {
        return this.comments.find { it.type == InspectionType.ERROR } != null
    }
}
