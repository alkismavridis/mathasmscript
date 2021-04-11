package eu.alkismavridis.mathasmscript.theory.usecases


fun getPackageName(fullName:String) : String {
    val lastDotIndex = fullName.lastIndexOf(".")
    if (lastDotIndex < 0) return ""
    return fullName.substring(0, lastDotIndex)
}
