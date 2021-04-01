package eu.alkismavridis.mathasmscript.repo.names

fun getSimpleNameOf(fullName:String) : String {
    return fullName.substring( fullName.lastIndexOf(".") + 1)
}
