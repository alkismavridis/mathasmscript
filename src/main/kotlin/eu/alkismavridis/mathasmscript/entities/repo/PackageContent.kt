package eu.alkismavridis.mathasmscript.entities.repo

class PackageContent(
        val packageData: MasPackage,
        val statements:Collection<FixedMasStatement>,
        val packages:Collection<MasPackage>
)
