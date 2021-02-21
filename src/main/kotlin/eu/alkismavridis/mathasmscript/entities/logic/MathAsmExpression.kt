package eu.alkismavridis.mathasmscript.entities.logic


class MathAsmExpression {
    var words: LongArray; private set
    var length: Int; private set


    constructor(words: LongArray, cloneInput: Boolean) {
        this.words = if (cloneInput) words.clone() else words
        this.length = words.size
    }

    constructor(other: MathAsmExpression) {
        this.words = other.words.sliceArray(0 until other.length)
        this.length = other.length
    }

    fun match(searchTerm: MathAsmExpression, pos: Int): Boolean {
        val srchLen = searchTerm.length
        if (pos + srchLen > this.length) return false

        val words2 = searchTerm.words
        var p = pos
        for (i in 0 until srchLen) {
            if (words[p] != words2[i]) return false
            p++
        }

        return true
    }

    fun select(searchTerm: MathAsmExpression, sel: ExpressionSelection) {
        val words2: LongArray = searchTerm.words
        val phrsLen: Int = searchTerm.length
        val first = words2[0]

        var srchIndex = 0
        var srchTester: Int
        val srcLimit: Int = length - phrsLen

        sel.clear()
        A@ while (true) {
            B@ while (true) {                    //1. search for first symbol
                if (srchIndex > srcLimit) break@A
                if (words[srchIndex] == first) break@B    //symbol found!
                srchIndex += 1
            }

            srchTester = srchIndex + 1
            for (index in 1 until phrsLen) {            //2. check other symbols
                if (words[srchTester] != words2[index]) {
                    srchIndex++
                    continue@A  //if failed to match, move the pointer one step and start again.
                } else srchTester++
            }

            sel.add(srchIndex)
            srchIndex = srchTester //move the pointer to the next search point
        }
    }

    fun replace(newExp: MathAsmExpression, oldExpresionLength: Int, selection: ExpressionSelection) {
        if (selection.length == 0) {
            return
        }

        val lengthDifference: Int = newExp.length - oldExpresionLength
        val totalLengthDiff: Int = lengthDifference * selection.length

        if (lengthDifference == 0) {
            this.replaceEqualSize(newExp, oldExpresionLength, selection)
        } else if (lengthDifference < 0) {
            this.replaceToSmallerSize(newExp, lengthDifference, selection)
        } else if (length + totalLengthDiff <= words.size) {
            this.replaceToGreaterSizeWithoutCopying(newExp, oldExpresionLength, totalLengthDiff, selection)
        } else {
            this.replaceToGreaterSizeWithCopying(newExp, oldExpresionLength, totalLengthDiff, selection)
        }
    }

    private fun replaceEqualSize(newExpression: MathAsmExpression, oldExpressionLength: Int, selection: ExpressionSelection) {
        val newWords = newExpression.words

        for (selectionIndex in 0 until selection.length) {
            var selectionPosition = selection.positions[selectionIndex]
            for (oldExpressionPosition in 0 until oldExpressionLength) {
                words[selectionPosition] = newWords[oldExpressionPosition]
                selectionPosition += 1
            }
        }
    }

    private fun replaceToSmallerSize(newExp: MathAsmExpression, lengthDifference: Int, selection: ExpressionSelection) {
        val newExpressionLength = newExp.length
        var currentSelectionPosition = selection.positions[0]

        for (selectionIndex in 0 until selection.length) {
            copyPart(newExp.words, 0, newExpressionLength, words, currentSelectionPosition)

            currentSelectionPosition += newExpressionLength
            val sourceReplacementIndex = currentSelectionPosition - lengthDifference * (selectionIndex + 1)
            val amountOfSymbolsToMove =
                    if (selectionIndex < selection.length - 1) selection.positions[selectionIndex + 1] - sourceReplacementIndex
                    else length - sourceReplacementIndex

            moveInternally(words, sourceReplacementIndex, currentSelectionPosition, amountOfSymbolsToMove)

            currentSelectionPosition += amountOfSymbolsToMove
        }

        this.length += lengthDifference * selection.length
    }

    private fun replaceToGreaterSizeWithoutCopying(
            newExp: MathAsmExpression,
            oldExpresionLength: Int,
            totalDif: Int,
            selection: ExpressionSelection
    ) {
        val newExpressionLength = newExp.length
        var targetIndex = this.length + totalDif - 1
        for (selectionPositionIndex in selection.length - 1 downTo 0) {
            val sourceIndex =
                    if (selectionPositionIndex == selection.length - 1) this.length - 1
                    else selection.positions[selectionPositionIndex + 1] - 1

            val symbolsToTransferCount = sourceIndex - selection.positions[selectionPositionIndex] - oldExpresionLength + 1
            moveInternallyInv(words, sourceIndex, targetIndex, symbolsToTransferCount)
            targetIndex -= symbolsToTransferCount

            copyPart(newExp.words, 0, newExpressionLength, words, targetIndex + 1 - newExpressionLength)
            targetIndex -= newExpressionLength
        }

        this.length += totalDif
    }

    private fun replaceToGreaterSizeWithCopying(
            newExpression: MathAsmExpression,
            oldExpresionLength: Int,
            totalLengthDiff: Int,
            selection: ExpressionSelection
    ) {
        val newExpressionLength = newExpression.length
        val tmpArray = LongArray(length + totalLengthDiff + SIZE_INCREMENT)
        var sourceIndex = length + totalLengthDiff - 1
        for (k in selection.length - 1 downTo 0) {
            val lastSourceAddress = if (k == selection.length - 1) length - 1 else selection.positions[k + 1] - 1
            val symbolsToTransferCount = lastSourceAddress - selection.positions[k] - oldExpresionLength + 1

            copyPart(
                    this.words,
                    lastSourceAddress - symbolsToTransferCount + 1,
                    symbolsToTransferCount,
                    tmpArray,
                    sourceIndex - symbolsToTransferCount + 1
            )

            sourceIndex -= symbolsToTransferCount
            copyPart(newExpression.words, 0, newExpressionLength, tmpArray, sourceIndex - newExpressionLength + 1)
            sourceIndex -= newExpressionLength
        }

        //copy beginning
        copyPart(this.words, 0, sourceIndex + 1, tmpArray, 0)
        this.words = tmpArray
        this.length += totalLengthDiff
    }


    fun getCapacity() = words.size

    fun ensureCapacity(newCapacity: Int) {
        if (newCapacity <= words.size) return
        this.words = this.words.copyOf(newCapacity)
    }

    fun saveSpace() {
        if (this.words.size == this.length) return
        this.words = this.words.copyOf(this.length)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("\"")

        for (i in 0 until length) {
            builder.append("${words[i]} ")
        }
        builder.append("\"")

        return builder.toString()
    }


    // REPLACING
    private fun moveInternally(wrd: LongArray, src: Int, targ: Int, len: Int) {
        var lenV = len
        var targV = targ
        var srcV = src

        while (lenV > 0) {
            wrd[targV] = wrd[srcV]
            targV += 1
            srcV += 1
            lenV -= 1
        }
    }


    private fun moveInternallyInv(wrd: LongArray, src: Int, targ: Int, len: Int) {
        var lenV = len
        var targV = targ
        var srcV = src

        while (lenV > 0) {
            wrd[targV] = wrd[srcV]
            targV -= 1
            srcV -= 1
            lenV -= 1
        }
    }


    private fun copyPart(src: LongArray, srcStart: Int, srcLen: Int, trg: LongArray, trgInd: Int) {
        var srcInd: Int = srcStart
        var trgIndV: Int = trgInd
        var srcLenV: Int = srcLen

        while (srcLenV > 0) {
            trg[trgIndV] = src[srcInd]
            srcLenV -= 1
            trgIndV += 1
            srcInd += 1
        }
    }
}

private const val SIZE_INCREMENT: Int = 100
