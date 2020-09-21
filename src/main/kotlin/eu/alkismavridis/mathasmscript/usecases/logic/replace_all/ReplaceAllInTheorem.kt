package eu.alkismavridis.mathasmscript.usecases.logic.replace_all

import eu.alkismavridis.mathasmscript.model.logic.*
import eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality.assertBaseLegality
import eu.alkismavridis.mathasmscript.usecases.logic.assert_statement_mutable.assertStatementMutability
import eu.alkismavridis.mathasmscript.usecases.logic.get_open_theorem.getOpenTheorem

fun replaceAll(target:MathAsmStatement, base: MathAsmStatement, sel: LogicSelection, nameForClones:String) : MathAsmStatement {
    val targetToEdit = getOpenTheorem(target, nameForClones)
    assertReplaceAllLegality(base, targetToEdit)

    val toBeReplaced = base.left
    targetToEdit.left.select(toBeReplaced, sel.side1)
    targetToEdit.right.select(toBeReplaced, sel.side2)

    val replacement = base.right
    if (sel.side1.length > 0) {
        targetToEdit.left.replace(replacement, toBeReplaced.length, sel.side1)
    }

    if (sel.side2.length > 0) {
        targetToEdit.right.replace(replacement, toBeReplaced.length, sel.side2)
    }

    return targetToEdit
}

private fun assertReplaceAllLegality(base: MathAsmStatement, target:MathAsmStatement) {
    assertBaseLegality(base.type)
    assertStatementMutability(target.type)

    if (!target.isBidirectional && base.grade <= target.grade) {
        throw IllegalReplaceAllException(
                "Cannot use ${base.name} as a base to replace-all. Base's grade should ba larger than unidirectional target's. Found: base ${base.grade} - ${target.grade} target"
        )
    }
}
