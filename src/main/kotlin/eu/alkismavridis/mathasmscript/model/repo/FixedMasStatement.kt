package eu.alkismavridis.mathasmscript.model.repo

import eu.alkismavridis.mathasmscript.model.logic.StatementType

class FixedMasStatement (
        val path:String,
        var packageId: Long,
        val type:StatementType,
        val text:String,
        var id: Long
)
