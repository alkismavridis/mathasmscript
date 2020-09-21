package eu.alkismavridis.mathasmscript.usecases.repo.get_package_contents

import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.repo.PackageContent
import eu.alkismavridis.mathasmscript.model.repo.PackageRepository
import eu.alkismavridis.mathasmscript.model.repo.StatementRepository

class GetPackageContents {
    companion object {
        fun get(packageName: String, packageRepo:PackageRepository, stmtRepo: StatementRepository) : PackageContent {
            packageRepo.find(packageName) ?: throw MathAsmException("$packageName does not exists")

            val statements = stmtRepo.findAllByPackage(packageName)
            val subPackages = packageRepo.findAllByParent(packageName)

            return PackageContent(statements, subPackages)
        }
    }
}
