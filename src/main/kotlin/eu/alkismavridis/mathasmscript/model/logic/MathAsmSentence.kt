package eu.alkismavridis.mathasmscript.model.logic



class MathAsmSentence {
    companion object {
        private const val SIZE_INCREMENT: Int = 100
    }


    var words: LongArray; private set
    var length: Int; private set


    constructor(words: LongArray, cloneInput: Boolean) {
        this.words = if (cloneInput) words.clone() else words
        this.length = words.size
    }

    constructor(other:MathAsmSentence) {
        this.words = other.words.sliceArray(0 until other.length)
        this.length = other.length
    }


    //SECTION SPACE MANAGEMENT
    fun getCapacity() = words.size

    fun ensureCapacity(newCapacity: Int) {
        if (newCapacity <= words.size) return
        this.words = this.words.copyOf(newCapacity)
    }

    fun saveSpace() {
        if (this.words.size == this.length) return
        this.words = this.words.copyOf(this.length)
    }


    //SECTION MATCHING
    fun match(searchTerm: MathAsmSentence, pos: Int) : Boolean {
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

    fun select(searchTerm: MathAsmSentence, sel: SentenceSelection) {
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


    //SECTION REPLACING
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


    fun replace(newPhrase: MathAsmSentence, oldPhraseLen: Int, sel: SentenceSelection) {
        val words2 = newPhrase.words
        val newPhraseLen: Int = newPhrase.length
        val selLen: Int = sel.length
        val phraseDif: Int = newPhraseLen - oldPhraseLen
        val totalDif: Int = phraseDif * selLen

        if (selLen == 0) return

        val selectionPositions = sel.positions
        if (phraseDif == 0) {
            for (k in 0 until selLen) {
                var i = selectionPositions[k]
                for (j in 0 until oldPhraseLen) {
                    words[i] = words2[j]
                    i += 1
                }
            }
        } else if (phraseDif < 0) { //new is smaller
            var i = selectionPositions[0]        //we point to the first match
            for (k in 0 until selLen) {
                copyPart(words2, 0, newPhraseLen, words, i)

                i += newPhraseLen                                    //i points to the symbol after the replacment (src)
                val j = i - phraseDif * (k + 1)                                //j points to the symbol after the match (trg)
                val h = if (k < selLen - 1) selectionPositions[k + 1] - j else length - j    //number of symbols to be moved
                moveInternally(words, j, i, h)

                i += h        //update for next copying
            }
        } else if (length + totalDif <= words.size) {
            var i = length + totalDif - 1                                        //i = last target address
            for (k in selLen - 1 downTo 0) {                                //k = sel array index
                val j = if (k == selLen - 1) length - 1 else selectionPositions[k + 1] - 1            //j = last source address
                val h = j - selectionPositions[k] - oldPhraseLen + 1                        //h is how many symbols to be transfered
                moveInternallyInv(words, j, i, h)
                i -= h                                                    //update for next copying

                copyPart(words2, 0, newPhraseLen, words, i + 1 - newPhraseLen)
                i -= newPhraseLen                                         //update for next copying
            }
        } else {
            val tmp = LongArray(length + totalDif + SIZE_INCREMENT)
            var i = length + totalDif - 1
            for (k in selLen - 1 downTo 0) {
                val j = if (k == selLen - 1) length - 1 else selectionPositions[k + 1] - 1                    //j = last source address
                val h = j - selectionPositions[k] - oldPhraseLen + 1                            //h is how many symbols to be transfered

                copyPart(words, j - h + 1, h, tmp, i - h + 1)
                i -= h                                                        //update for next copying

                copyPart(words2, 0, newPhraseLen, tmp, i - newPhraseLen + 1)
                i -= newPhraseLen
            }

            //copy begining
            copyPart(words, 0, i + 1, tmp, 0)
            words = tmp
        }

        length += totalDif
    }


    //SECTION SERIALIZATION
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("\"")

        for (i in 0 until length) {
            builder.append("${words[i]} ")
        }
        builder.append("\"")

        return builder.toString()
    }
}
