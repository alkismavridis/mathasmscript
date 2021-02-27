package eu.alkismavridis.mathasmscript.usecases.parser

import eu.alkismavridis.mathasmscript.entities.repo.MasPackage
import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import eu.alkismavridis.mathasmscript.usecases.names.mergeNames
import eu.alkismavridis.mathasmscript.usecases.names.validations.assertPackagePathValid
import eu.alkismavridis.mathasmscript.usecases.repo.assertPackageNotExists
import eu.alkismavridis.mathasmscript.usecases.repo.getOrCreatePackage
import java.time.Instant

fun createPackageUseCase(parentPath: String, packageName: String, theoryId: Long, packageRepo: PackageRepository) : MasPackage {
    val newPath = mergeNames(parentPath, packageName)
    assertPackagePathValid(newPath)
    assertPackageNotExists(newPath, theoryId, packageRepo)

    return getOrCreatePackage(theoryId, newPath, Instant.now(), packageRepo)
}
