package eu.alkismavridis.mathasmscript.theory.internal

import eu.alkismavridis.mathasmscript.theory.repo.PackageRepository

fun assertPackageNotExists(path: String, theoryId: Long, packageRepo: PackageRepository) {
    val alreadyExistingPackage = packageRepo.find(path, theoryId)
    if (alreadyExistingPackage != null) {
        throw IllegalStateException("Package newPath already exists in theory $theoryId")
    }
}
