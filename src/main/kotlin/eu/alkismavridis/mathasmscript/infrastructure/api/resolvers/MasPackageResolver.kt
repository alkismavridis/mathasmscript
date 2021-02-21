package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.entities.repo.MasPackage
import eu.alkismavridis.mathasmscript.usecases.parser.get_simple_name.GetSimpleName
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class MasPackageResolver : GraphQLResolver<MasPackage> {
    fun name(pack: MasPackage) : String {
        return GetSimpleName.get(pack.path)
    }
}
