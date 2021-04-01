package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.repo.MasPackage
import eu.alkismavridis.mathasmscript.repo.names.getSimpleNameOf
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class MasPackageResolver : GraphQLResolver<MasPackage> {
    fun name(pack: MasPackage) : String {
        return getSimpleNameOf(pack.path)
    }
}
