package eu.alkismavridis.mathasmscript.usecases.parser.get_simple_name


class GetSimpleName {
    companion object {
        fun get(fullName:String) : String {
            return fullName.substring( fullName.lastIndexOf(".") + 1)
        }
    }
}
