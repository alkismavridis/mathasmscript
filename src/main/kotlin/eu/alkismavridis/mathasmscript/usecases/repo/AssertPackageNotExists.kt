package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import java.lang.IllegalStateException

fun assertPackageNotExists(path: String, theoryId: Long, packageRepo: PackageRepository) {
    val alreadyExistingPackage = packageRepo.find(path, theoryId)
    if (alreadyExistingPackage != null) {
        throw IllegalStateException("Package newPath already exists in theory $theoryId")
    }
}
