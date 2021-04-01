package eu.alkismavridis.mathasmscript.parser

import eu.alkismavridis.mathasmscript.core.MathAsmStatement
import eu.alkismavridis.mathasmscript.core.exceptions.MathAsmException
import java.io.PrintStream


class SymbolNotFoundException(message:String) : MathAsmException(message) {}

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


    /// SERIALISATION
    fun write(stmt: MathAsmStatement, out:PrintStream) {
        for (i in 0 until stmt.left.length) {
            out.print( this.requireSymbol(stmt.left.words[i]) )
            out.print(" ")
        }

        if (stmt.isBidirectional) {
            out.print( if(stmt.grade == 0) "<--->" else "<--${stmt.grade}-->" )
            out.print(" ")
        } else {
            out.print( if(stmt.grade == 0) "--->" else "---${stmt.grade}-->" )
            out.print(" ")
        }

        for (i in 0 until stmt.right.length) {
            out.print( this.requireSymbol(stmt.right.words[i]) )
            out.print(" ")
        }
    }

    fun toString(stmt: MathAsmStatement) : String {
        val builder = StringBuilder()
        for (i in 0 until stmt.left.length) {
            builder.append( this.requireSymbol(stmt.left.words[i]) ).append(" ")
        }

        if (stmt.isBidirectional) {
            builder.append( if(stmt.grade == 0) "<--->" else "<--${stmt.grade}-->" ).append(" ")
        } else {
            builder.append( if(stmt.grade == 0) "--->" else "---${stmt.grade}-->" ).append(" ")
        }

        for (i in 0 until stmt.right.length) {
            builder.append( this.requireSymbol(stmt.right.words[i]) ).append(" ")
        }

        return builder.toString()
    }
}
