package eu.alkismavridis.mathasmscript.theory.usecases

fun getSimpleName(fullName:String) : String {
    return fullName.substring( fullName.lastIndexOf(".") + 1)
}
