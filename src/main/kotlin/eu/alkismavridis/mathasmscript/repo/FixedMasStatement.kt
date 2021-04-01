package eu.alkismavridis.mathasmscript.repo

class FixedMasStatement (
        val path:String,
        var packageId: Long, // TODO remove this. DB layer only. Not in entity.
        val type: FixedStatementType,
        val text:String,
        val theoryId: Long, // TODO remove this. DB layer only. Not in entity
        val id: Long
)
