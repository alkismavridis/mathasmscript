package eu.alkismavridis.mathasmscript.repo.usecases

import eu.alkismavridis.mathasmscript.core.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.repo.PackageContent
import eu.alkismavridis.mathasmscript.repo.PackageRepository
import eu.alkismavridis.mathasmscript.repo.StatementRepository

fun getPackageContent(theoryId: Long, packageName: String, packageRepo: PackageRepository, stmtRepo: StatementRepository) : PackageContent {
    val targetPackage = packageRepo.find(packageName, theoryId) ?: throw MathAsmException("$packageName does not exist")

    val statements = stmtRepo.findAllByPackage(packageName, theoryId)
    val subPackages = packageRepo.findAllByParent(packageName, theoryId)

    return PackageContent(targetPackage, statements, subPackages)
}
