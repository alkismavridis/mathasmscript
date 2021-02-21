package eu.alkismavridis.mathasmscript.entities.repo

interface PackageRepository {
    fun find(fullName: String) : MasPackage?
    fun findExistingNames(paths:Collection<String>) : List<MasPackage>
    fun findAllByParent(path:String) : List<MasPackage>

    fun saveAll(packages:List<MasPackage>)
}
