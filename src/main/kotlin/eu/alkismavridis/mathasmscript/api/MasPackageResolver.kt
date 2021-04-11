package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.theory.model.MasPackage
import eu.alkismavridis.mathasmscript.theory.usecases.getSimpleName
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class MasPackageResolver : GraphQLResolver<MasPackage> {
    fun name(pack: MasPackage) : String {
        return getSimpleName(pack.path)
    }
}
