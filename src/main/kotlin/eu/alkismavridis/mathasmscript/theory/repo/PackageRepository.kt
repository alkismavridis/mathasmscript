package eu.alkismavridis.mathasmscript.theory.repo

import eu.alkismavridis.mathasmscript.theory.model.MasPackage

interface PackageRepository {
    fun find(fullName: String, theoryId: Long) : MasPackage?
    fun findExistingNames(paths:Collection<String>, theoryId: Long) : List<MasPackage>
    fun findAllByParent(path:String, theoryId: Long) : List<MasPackage>

    fun saveAll(packages:List<MasPackage>)
    fun existsByParent(path: String, theoryId: Long): Boolean
    fun delete(path: String, theoryId: Long): Boolean
}
