package eu.alkismavridis.mathasmscript.entities.logic

class FixedMasStatement (
        val path:String,
        var packageId: Long, // TODO remove this. DB layer only. Not in entity.
        val type:StatementType,
        val text:String,
        val theoryId: Long, // TODO remove this. DB layer only. Not in entity
        val id: Long
)
