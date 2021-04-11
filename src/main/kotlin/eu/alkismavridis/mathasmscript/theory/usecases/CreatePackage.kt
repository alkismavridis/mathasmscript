package eu.alkismavridis.mathasmscript.theory.usecases

import eu.alkismavridis.mathasmscript.theory.internal.assertPackageNotExists
import eu.alkismavridis.mathasmscript.theory.internal.mergeNames
import eu.alkismavridis.mathasmscript.theory.model.MasPackage
import eu.alkismavridis.mathasmscript.theory.repo.PackageRepository
import eu.alkismavridis.mathasmscript.theory.validations.PACKAGE_NAME_ERROR_MESSAGE
import eu.alkismavridis.mathasmscript.theory.validations.isPackageNameValid
import java.time.Instant

fun createPackage(parentPath: String, packageName: String, theoryId: Long, packageRepo: PackageRepository) : MasPackage {
    val newPath = mergeNames(parentPath, packageName)
    assertPackagePathValid(newPath)
    assertPackageNotExists(newPath, theoryId, packageRepo)

    return getOrCreatePackage(theoryId, newPath, Instant.now(), packageRepo)
}

private fun assertPackagePathValid(path: String) {
    if (!isPackagePathValid(path)) {
        throw IllegalArgumentException(PACKAGE_NAME_ERROR_MESSAGE)
    }
}

private fun isPackagePathValid(path: String) : Boolean {
    return path.split(".").none{ !isPackageNameValid(it) }
}
