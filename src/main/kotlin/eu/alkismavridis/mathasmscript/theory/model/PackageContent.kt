package eu.alkismavridis.mathasmscript.theory.model

class PackageContent(
        val packageData: MasPackage,
        val statements:Collection<FixedMasStatement>,
        val packages:Collection<MasPackage>
)
