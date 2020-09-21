package eu.alkismavridis.mathasmscript.usecases.parser.get_parent_package


class GetParentPackage {
    companion object {
        fun get(fullName:String) : String {
            val lastDotIndex = fullName.lastIndexOf(".")
            if (lastDotIndex < 0) return ""
            return fullName.substring(0, lastDotIndex)
        }
    }
}
