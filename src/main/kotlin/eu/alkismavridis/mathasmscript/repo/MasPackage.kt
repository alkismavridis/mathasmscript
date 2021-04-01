package eu.alkismavridis.mathasmscript.repo

import java.time.Instant

class MasPackage(
        var id: Long,
        val theoryId: Long,
        val path: String,
        val parentId: Long?,
        val createdAt: Instant
)
