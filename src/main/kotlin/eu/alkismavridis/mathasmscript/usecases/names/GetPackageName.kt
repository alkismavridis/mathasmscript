package eu.alkismavridis.mathasmscript.usecases.names


fun getPackageNameOf(fullName:String) : String {
    val lastDotIndex = fullName.lastIndexOf(".")
    if (lastDotIndex < 0) return ""
    return fullName.substring(0, lastDotIndex)
}
