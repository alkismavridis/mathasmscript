package eu.alkismavridis.mathasmscript.usecases.names

fun mergeNames(first: String, second: String) : String {
    return when {
        first.isEmpty() -> second
        second.isEmpty() -> first
        else -> "$first.$second"
    }
}
