package eu.alkismavridis.mathasmscript.usecases.names

fun getSimpleNameOf(fullName:String) : String {
    return fullName.substring( fullName.lastIndexOf(".") + 1)
}
