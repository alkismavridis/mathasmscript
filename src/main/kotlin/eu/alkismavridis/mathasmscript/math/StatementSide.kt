package eu.alkismavridis.mathasmscript.math

enum class StatementSide(private val value:Byte) {
    LEFT(1),
    RIGHT(2),
    BOTH(3);

    fun getValue():Byte = value
}
