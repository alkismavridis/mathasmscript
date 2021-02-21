package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.repo.PackageContent
import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository

fun getPackageContent(theoryId: Long, packageName: String, packageRepo:PackageRepository, stmtRepo: StatementRepository) : PackageContent {
    packageRepo.find(packageName, theoryId) ?: throw MathAsmException("$packageName does not exists")

    val statements = stmtRepo.findAllByPackage(packageName, theoryId)
    val subPackages = packageRepo.findAllByParent(packageName, theoryId)

    return PackageContent(statements, subPackages)
}
