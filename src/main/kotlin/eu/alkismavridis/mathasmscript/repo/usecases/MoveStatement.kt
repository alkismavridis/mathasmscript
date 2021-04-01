package eu.alkismavridis.mathasmscript.repo.usecases

import eu.alkismavridis.mathasmscript.core.FixedMasStatement
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.repo.PackageRepository
import eu.alkismavridis.mathasmscript.repo.StatementRepository
import eu.alkismavridis.mathasmscript.repo.names.getPackageNameOf
import eu.alkismavridis.mathasmscript.repo.names.getSimpleNameOf
import eu.alkismavridis.mathasmscript.repo.names.validations.assertAxiomNameValid
import eu.alkismavridis.mathasmscript.repo.names.validations.assertPackagePathValid
import eu.alkismavridis.mathasmscript.repo.names.validations.assertTheoremNameValid
import java.time.Instant

fun moveStatement(theoryId: Long, currentPath:String, newPath:String, stmtRepo: StatementRepository, packageRepo: PackageRepository) : FixedMasStatement {
    val currentStatement = stmtRepo.findByPath(currentPath, theoryId)
            ?: throw MathAsmException("Statement with path $currentPath not found in theory  $theoryId.")
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
