package eu.alkismavridis.mathasmscript.model.logic

open class MathAsmException : Exception {

    constructor(message: String, cause:Throwable) : super(message, cause) {}

    constructor(message: String) : super(message) {}
}
