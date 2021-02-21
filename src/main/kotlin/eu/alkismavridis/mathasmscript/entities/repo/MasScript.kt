package eu.alkismavridis.mathasmscript.entities.repo

import java.time.Instant

class MasScript(
        val contents:String,
        val fileName:String,
        val importedAt: Instant
)
