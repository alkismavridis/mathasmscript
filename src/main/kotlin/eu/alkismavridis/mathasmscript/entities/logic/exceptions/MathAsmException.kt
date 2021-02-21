package eu.alkismavridis.mathasmscript.entities.logic.exceptions

open class MathAsmException : Exception {
    constructor(message: String, cause:Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}
