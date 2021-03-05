package eu.alkismavridis.mathasmscript.usecases.repo

import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.repo.PackageRepository
import eu.alkismavridis.mathasmscript.entities.repo.StatementRepository
import eu.alkismavridis.mathasmscript.usecases.names.getPackageNameOf
import eu.alkismavridis.mathasmscript.usecases.names.getSimpleNameOf
import eu.alkismavridis.mathasmscript.usecases.names.validations.*
import java.time.Instant

fun moveStatement(theoryId: Long, currentPath:String, newPath:String, stmtRepo:StatementRepository, packageRepo:PackageRepository) : FixedMasStatement {
    val currentStatement = stmtRepo.findByPath(currentPath, theoryId)
            ?: throw MathAsmException("Statement with path $currentPath not foundtheoryId.")
    if (currentPath == newPath) return currentStatement

    val newPackageName = getPackageNameOf(newPath)
    validateNewName(currentStatement, newPackageName, getSimpleNameOf(newPath))

    val targetPackage = getOrCreatePackage(theoryId, newPackageName, Instant.now(), packageRepo)
    val newStatement = FixedMasStatement(
            newPath,
            targetPackage.id,
            currentStatement.type,
            currentStatement.text,
            currentStatement.theoryId,
            currentStatement.id
    )
    stmtRepo.update(newStatement, currentPath)


    return newStatement
}

private fun validateNewName(currentStatement: FixedMasStatement, newPackageName:String, newStatementName:String) {
    if (currentStatement.type == StatementType.AXIOM) {
        assertAxiomNameValid(newStatementName)
    } else {
        assertTheoremNameValid(newStatementName)
    }

    assertPackagePathValid(newPackageName)
}
