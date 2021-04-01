package eu.alkismavridis.mathasmscript.parser.result

enum class InspectionType {
    /** Indicates internal errors of the MasServer, not error in the script. */
    APP_ERROR,

    /** Indicates an error in the mas script */
    ERROR,

    WARNING,
    INFO,
    DEBUG
}
