package eu.alkismavridis.mathasmscript.math.internal.transformations

import eu.alkismavridis.mathasmscript.math.StatementSide
import eu.alkismavridis.mathasmscript.math.internal.model.LogicSelection
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException
import eu.alkismavridis.mathasmscript.math.internal.model.MutableMathAsmStatement
import eu.alkismavridis.mathasmscript.math.internal.utils.assertBaseLegality
import eu.alkismavridis.mathasmscript.math.internal.utils.assertStatementMutability
import eu.alkismavridis.mathasmscript.math.internal.utils.getOpenTheorem

fun replaceAllInSentence(target: MutableMathAsmStatement, sideToEdit: StatementSide, base: MutableMathAsmStatement, sel: LogicSelection, nameForClones:String) : MutableMathAsmStatement {
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
private fun assertReplaceSentenceLegality(target: MutableMathAsmStatement, sideToEdit: StatementSide, base: MutableMathAsmStatement) {
    assertBaseLegality(base.type)
    assertStatementMutability(target.type)

    if (base.grade > target.grade) {
        throw IllegalSentenceReplacementException("Cannot perform sentence-replacement because base \"${base.name}\" has larger grade than the target \"${target.name}\"")
    }

    if (sideToEdit == StatementSide.LEFT && !target.isBidirectional) {
        throw IllegalSentenceReplacementException("Cannot edit left sentence of unidirectional target ${target.name}")
    }
}

class IllegalSentenceReplacementException(message:String) : MathAsmException(message)
