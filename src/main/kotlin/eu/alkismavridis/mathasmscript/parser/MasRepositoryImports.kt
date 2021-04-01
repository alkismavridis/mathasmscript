package eu.alkismavridis.mathasmscript.parser


class MasRepositoryImports(val url:String, val line:Int, val column:Int) {
    /** key: local name, value: official fullName */
    val variables = mutableMapOf<NameToken, String>()

    fun getLocalNameFor(officialFullName:String) : NameToken? {
        this.variables.forEach{
            if (it.value == officialFullName) return it.key
        }

        return null
    }
}
