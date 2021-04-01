package eu.alkismavridis.mathasmscript.repo.usecases

import eu.alkismavridis.mathasmscript.repo.PackageRepository

fun assertPackageNotExists(path: String, theoryId: Long, packageRepo: PackageRepository) {
    val alreadyExistingPackage = packageRepo.find(path, theoryId)
    if (alreadyExistingPackage != null) {
        throw IllegalStateException("Package newPath already exists in theory $theoryId")
    }
}
