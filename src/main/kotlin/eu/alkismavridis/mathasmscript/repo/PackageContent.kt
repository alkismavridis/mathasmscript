package eu.alkismavridis.mathasmscript.repo

import eu.alkismavridis.mathasmscript.core.FixedMasStatement

class PackageContent(
        val packageData: MasPackage,
        val statements:Collection<FixedMasStatement>,
        val packages:Collection<MasPackage>
)
