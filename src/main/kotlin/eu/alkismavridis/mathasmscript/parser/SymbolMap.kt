package eu.alkismavridis.mathasmscript.parser

class SymbolNotFoundException(message:String) : ParserException(message)

class SymbolMap {
    private val textToId = mutableMapOf<String, Long>()
    private val idToText = mutableMapOf<Long, String>()
    private var nextId = 1L


    fun getSymbol(id:Long) = this.idToText[id]
    fun getId(text:String) = this.textToId[text]

    fun getOrCreateId(text:String) : Long {
        val alreadyExisting = this.textToId[text]
        if (alreadyExisting != null) return alreadyExisting

        val newId = this.nextId
        this.nextId++
        this.textToId[text] = newId
        this.idToText[newId] = text

        return newId
    }

    fun requireSymbol(id:Long) : String {
        val result = this.idToText[id]
        if (result != null) return result

        throw SymbolNotFoundException("Symbol with id $id not found")
    }
}
