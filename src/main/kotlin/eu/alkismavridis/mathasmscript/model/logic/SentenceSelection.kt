package eu.alkismavridis.mathasmscript.model.logic

class SentenceSelection(capacity: Int, val sizeIncrement: Int = 100) {
    var length:Int = 0; private set
    var positions:IntArray = IntArray(capacity); private set


    fun clear() {
        this.length = 0
    }

    fun add(pos:Int) {
        if (length >= this.positions.size)  {
            ensureCapacity(this.length + this.sizeIncrement)
        }

        this.positions[this.length] = pos
        this.length++
    }

    private fun ensureCapacity(newCapacity:Int) {
        if (newCapacity <= this.positions.size) return
        this.positions = this.positions.copyOf(newCapacity)
    }
}
