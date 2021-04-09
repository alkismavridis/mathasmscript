package eu.alkismavridis.mathasmscript.math.internal.transformations

import eu.alkismavridis.mathasmscript.math.internal.model.LogicSelection
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.math.internal.utils.assertBaseLegality
import eu.alkismavridis.mathasmscript.math.internal.utils.assertStatementMutability
import eu.alkismavridis.mathasmscript.math.internal.utils.getOpenTheorem

fun handleReplaceAll(target: MutableMathAsmStatement, base: MutableMathAsmStatement, sel: LogicSelection, nameForClones:String) : MutableMathAsmStatement {
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

private fun assertReplaceAllLegality(base: MutableMathAsmStatement, target: MutableMathAsmStatement) {
    assertBaseLegality(base.type)
    assertStatementMutability(target.type)

    if (!target.isBidirectional && base.grade <= target.grade) {
        throw MathAsmException(
                "Cannot use ${base.name} as a base to replace-all. Base's grade should ba larger than unidirectional target's. Found: base ${base.grade} - ${target.grade} target"
        )
    }
}
