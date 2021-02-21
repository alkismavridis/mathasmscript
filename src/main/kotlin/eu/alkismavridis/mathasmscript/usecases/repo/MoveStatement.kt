package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.parser.get_parent_package.GetParentPackage
import eu.alkismavridis.mathasmscript.usecases.parser.get_simple_name.GetSimpleName
import eu.alkismavridis.mathasmscript.usecases.parser.validate_package_part_name.ValidatePackagePartName
import eu.alkismavridis.mathasmscript.usecases.parser.validate_theorem_name.ValidateStatementName
import java.time.Instant

fun moveStatement(currentPath:String, newPath:String, stmtRepo:StatementRepository, packageRepo:PackageRepository) : FixedMasStatement {
    val currentStatement = stmtRepo.findByPath(currentPath) ?: throw MathAsmException("Statement with path $currentPath not found.")
    if (currentPath == newPath) return currentStatement

    val newPackageName = GetParentPackage.get(newPath)
    validateNewName(currentStatement, newPackageName, GetSimpleName.get(newPath))

    val targetPackage = getOrCreatePackage(newPackageName, Instant.now(), packageRepo)
    val newStatement = FixedMasStatement(
            newPath,
            targetPackage.id,
            currentStatement.type,
            currentStatement.text,
            currentStatement.id
    )
    stmtRepo.update(newStatement, currentPath)


    return newStatement
}

private fun validateNewName(currentStatement: FixedMasStatement, newPackageName:String, newStatementName:String) {
    if (currentStatement.type == StatementType.AXIOM) {
        if (!ValidateStatementName.isAxiomNameValid(newStatementName)) {
            throw MathAsmException(ValidateStatementName.INVALID_AXIOM_MESSAGE)
        }
    } else {
        if (!ValidateStatementName.isTheoremNameValid(newStatementName)) {
            throw MathAsmException(ValidateStatementName.INVALID_THEOREM_MESSAGE)
        }
    }

    val newPackageNameParts = newPackageName.split('.')
    newPackageNameParts.forEach {
        if (!ValidatePackagePartName.isPackagePartNameValid(it)) {
            throw MathAsmException(ValidatePackagePartName.ERROR_MESSAGE)
        }
    }
}
