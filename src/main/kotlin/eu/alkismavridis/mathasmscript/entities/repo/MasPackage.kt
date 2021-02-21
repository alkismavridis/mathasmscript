package eu.alkismavridis.mathasmscript.entities.repo

import java.time.Instant

class MasPackage(
        val path: String,
        val parentId: Long?,
        val createdAt: Instant,
        var id: Long
)
