package eu.alkismavridis.mathasmscript.theory.internal

fun mergeNames(first: String, second: String) : String {
    return when {
        first.isEmpty() -> second
        second.isEmpty() -> first
        else -> "$first.$second"
    }
}
