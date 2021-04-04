package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.LogicSelection
import eu.alkismavridis.mathasmscript.core.MathAsmStatement
import eu.alkismavridis.mathasmscript.core.internal.IllegalReplaceAllException
import eu.alkismavridis.mathasmscript.core.internal.assertBaseLegality
import eu.alkismavridis.mathasmscript.core.internal.assertStatementMutability
import eu.alkismavridis.mathasmscript.core.internal.getOpenTheorem

fun replaceAll(target: MathAsmStatement, base: MathAsmStatement, sel: LogicSelection, nameForClones:String) : MathAsmStatement {
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

private fun assertReplaceAllLegality(base: MathAsmStatement, target: MathAsmStatement) {
    assertBaseLegality(base.type)
    assertStatementMutability(target.type)

    if (!target.isBidirectional && base.grade <= target.grade) {
        throw IllegalReplaceAllException(
                "Cannot use ${base.name} as a base to replace-all. Base's grade should ba larger than unidirectional target's. Found: base ${base.grade} - ${target.grade} target"
        )
    }
}
