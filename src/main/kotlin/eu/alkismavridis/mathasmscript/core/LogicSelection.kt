package eu.alkismavridis.mathasmscript.core

import eu.alkismavridis.mathasmscript.core.internal.ExpressionSelection

class LogicSelection(sideCapacity:Int) {
    val side1 = ExpressionSelection(sideCapacity)
    val side2 = ExpressionSelection(sideCapacity)
}
