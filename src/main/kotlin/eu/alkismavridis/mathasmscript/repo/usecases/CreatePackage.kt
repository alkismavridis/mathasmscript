package eu.alkismavridis.mathasmscript.repo.usecases

import eu.alkismavridis.mathasmscript.repo.MasPackage
import eu.alkismavridis.mathasmscript.repo.PackageRepository
import eu.alkismavridis.mathasmscript.repo.names.mergeNames
import eu.alkismavridis.mathasmscript.repo.names.validations.assertPackagePathValid
import java.time.Instant

fun createPackageUseCase(parentPath: String, packageName: String, theoryId: Long, packageRepo: PackageRepository) : MasPackage {
    val newPath = mergeNames(parentPath, packageName)
    assertPackagePathValid(newPath)
    assertPackageNotExists(newPath, theoryId, packageRepo)

    return getOrCreatePackage(theoryId, newPath, Instant.now(), packageRepo)
}
