package eu.alkismavridis.mathasmscript.model.logic


class MathAsmStatement(
        val name:String,
        val type: StatementType,
        left: MathAsmSentence,
        right: MathAsmSentence,
        val isBidirectional: Boolean,
        val grade: Int = 0
) {
    var left: MathAsmSentence = left; private set
    var right: MathAsmSentence = right; private set


    /// MODIFIERS
    fun saveSpace() {
        this.left.saveSpace()
        this.right.saveSpace()
    }

    fun reverse() {
        val tmp = this.left
        this.left = this.right
        this.right = tmp
    }


    /// SERIALIZATION
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append('"')

        val words1: LongArray = left.words
        for (i in 0 until left.length) {
            builder.append("${words1[i]} ")
        }

        if (this.isBidirectional) {
            builder.append(if (this.grade == 0)
                "<--->" else
                "<--${this.grade}-->"
            )
        } else {
            builder.append(if (this.grade == 0)
                "--->" else
                "---${this.grade}-->"
            )
        }

        val words2: LongArray = right.words
        for (i in 0 until right.length) {
            builder.append("${words2[i]} ")
        }

        builder.append('"')
        return builder.toString()
    }
}
