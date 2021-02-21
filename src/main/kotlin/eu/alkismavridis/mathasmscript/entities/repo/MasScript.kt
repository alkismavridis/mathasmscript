package eu.alkismavridis.mathasmscript.entities.repo

import java.time.Instant

class MasScript(
        val theoryId: Long,
        val contents:String,
        val fileName:String,
        val importedAt: Instant
)
