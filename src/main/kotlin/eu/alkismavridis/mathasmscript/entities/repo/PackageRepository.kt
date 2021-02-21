package eu.alkismavridis.mathasmscript.entities.repo

interface PackageRepository {
    fun find(fullName: String, theoryId: Long) : MasPackage?
    fun findExistingNames(paths:Collection<String>, theoryId: Long) : List<MasPackage>
    fun findAllByParent(path:String, theoryId: Long) : List<MasPackage>

    fun saveAll(packages:List<MasPackage>)
}
