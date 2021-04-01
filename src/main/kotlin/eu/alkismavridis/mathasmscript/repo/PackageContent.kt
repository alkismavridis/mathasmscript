package eu.alkismavridis.mathasmscript.repo

class PackageContent(
        val packageData: MasPackage,
        val statements:Collection<FixedMasStatement>,
        val packages:Collection<MasPackage>
)
