package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.entities.repo.MasPackage
import eu.alkismavridis.mathasmscript.usecases.names.getSimpleNameOf
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class MasPackageResolver : GraphQLResolver<MasPackage> {
    fun name(pack: MasPackage) : String {
        return getSimpleNameOf(pack.path)
    }
}
