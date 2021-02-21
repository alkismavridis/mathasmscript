package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.repo.PackageContent
import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository

fun getPackageContent(packageName: String, packageRepo:PackageRepository, stmtRepo: StatementRepository) : PackageContent {
    packageRepo.find(packageName) ?: throw MathAsmException("$packageName does not exists")

    val statements = stmtRepo.findAllByPackage(packageName)
    val subPackages = packageRepo.findAllByParent(packageName)

    return PackageContent(statements, subPackages)
}
