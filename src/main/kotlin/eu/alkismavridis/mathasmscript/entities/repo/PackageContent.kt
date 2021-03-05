package eu.alkismavridis.mathasmscript.entities.repo

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement

class PackageContent(
        val packageData: MasPackage,
        val statements:Collection<FixedMasStatement>,
        val packages:Collection<MasPackage>
)
