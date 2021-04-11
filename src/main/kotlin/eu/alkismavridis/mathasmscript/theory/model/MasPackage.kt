package eu.alkismavridis.mathasmscript.theory.model

import java.time.Instant

class MasPackage(
        var id: Long,
        val theoryId: Long,
        val path: String,
        val parentId: Long?,
        val createdAt: Instant
)
