package eu.alkismavridis.mathasmscript.theory.usecases

import eu.alkismavridis.mathasmscript.theory.model.PackageContent
import eu.alkismavridis.mathasmscript.theory.repo.PackageRepository
import eu.alkismavridis.mathasmscript.theory.repo.StatementRepository

fun getPackageContent(theoryId: Long, packageName: String, packageRepo: PackageRepository, stmtRepo: StatementRepository) : PackageContent {
    val targetPackage = packageRepo.find(packageName, theoryId) ?: throw IllegalArgumentException("$packageName does not exist")

    val statements = stmtRepo.findAllByPackage(packageName, theoryId)
    val subPackages = packageRepo.findAllByParent(packageName, theoryId)

    return PackageContent(targetPackage, statements, subPackages)
}
