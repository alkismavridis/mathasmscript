package eu.alkismavridis.mathasmscript.core.internal.rules

import eu.alkismavridis.mathasmscript.core.StatementSide
import eu.alkismavridis.mathasmscript.core.internal.*

fun replaceSingleMatch(target: MutableMathAsmStatement, sideToEdit: StatementSide, positionToReplace:Int, base: MutableMathAsmStatement, sel: LogicSelection, nameForClones:String) : MutableMathAsmStatement {
    val targetToEdit = getOpenTheorem(target, nameForClones)
    assertReplaceOneLegality(targetToEdit, sideToEdit, positionToReplace, base)

    val toBeReplaced = base.left
    val replacement = base.right
    if (sideToEdit == StatementSide.LEFT) {
        sel.side1.clear()
        sel.side2.clear()
        sel.side1.add(positionToReplace)
        targetToEdit.left.replace(replacement, toBeReplaced.length, sel.side1)
    } else {
        sel.side1.clear()
        sel.side2.clear()
        sel.side2.add(positionToReplace)
        targetToEdit.right.replace(replacement, toBeReplaced.length, sel.side2)
    }

    return targetToEdit
}


private fun assertReplaceOneLegality(target: MutableMathAsmStatement, sideToEdit: StatementSide, positionToReplace:Int, base: MutableMathAsmStatement) {
    assertBaseLegality(base.type)

    if (base.grade != 0) {
        throw IllegalSingleReplacementException("Only zero-grade bases can be used for single replacement, but ${base.name} had ${base.grade}")
    }

    val toBeReplaced = base.left
    if (sideToEdit == StatementSide.LEFT) {
        if (!target.isBidirectional) {
            throw IllegalSingleReplacementException("Cannot edit left side of unidirectional target ${target.name}")
        } else if (!target.left.match(toBeReplaced, positionToReplace)) {
            throw IllegalSingleReplacementException("Symbols did not match with base ${base.name}: ${target.name} -> left side -> position $positionToReplace")
        }
    } else if (!target.right.match(toBeReplaced, positionToReplace)) {
        throw IllegalSingleReplacementException("Symbols did not match with base ${base.name}: ${target.name} -> right side -> position $positionToReplace")
    }
}
