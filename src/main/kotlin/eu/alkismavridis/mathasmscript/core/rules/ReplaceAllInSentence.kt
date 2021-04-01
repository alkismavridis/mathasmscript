package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.LogicSelection
import eu.alkismavridis.mathasmscript.core.MathAsmStatement
import eu.alkismavridis.mathasmscript.core.StatementSide
import eu.alkismavridis.mathasmscript.core.exceptions.IllegalSentenceReplacementException

fun replaceAllInSentence(target: MathAsmStatement, sideToEdit: StatementSide, base: MathAsmStatement, sel: LogicSelection, nameForClones:String) : MathAsmStatement {
    val targetToEdit = getOpenTheorem(target, nameForClones)
    assertReplaceSentenceLegality(targetToEdit, sideToEdit, base)

    val toBeReplaced = base.left
    val replacement = base.right

    if (sideToEdit == StatementSide.LEFT) {
        sel.side2.clear()
        targetToEdit.left.select(toBeReplaced, sel.side1)
        targetToEdit.left.replace(replacement, toBeReplaced.length, sel.side1)
    } else {
        sel.side1.clear()
        targetToEdit.right.select(toBeReplaced, sel.side2)
        targetToEdit.right.replace(replacement, toBeReplaced.length, sel.side2)
    }

    return targetToEdit
}


/** Asserts the legality of move types MoveType_REPLACE_LEFT and MoveType_REPLACE_RIGHT */
private fun assertReplaceSentenceLegality(target: MathAsmStatement, sideToEdit: StatementSide, base: MathAsmStatement) {
    assertBaseLegality(base.type)
    assertStatementMutability(target.type)

    if (base.grade > target.grade) {
        throw IllegalSentenceReplacementException("Cannot perform sentence-replacement because base \"${base.name}\" has larger grade than the target \"${target.name}\"")
    }

    if (sideToEdit == StatementSide.LEFT && !target.isBidirectional) {
        throw IllegalSentenceReplacementException("Cannot edit left sentence of unidirectional target ${target.name}")
    }
}
