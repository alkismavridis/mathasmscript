package eu.alkismavridis.mathasmscript.entities.repo

import eu.alkismavridis.mathasmscript.entities.logic.StatementType

class FixedMasStatement (
        val path:String,
        var packageId: Long,
        val type:StatementType,
        val text:String,
        val theoryId: Long,
        val id: Long
)
