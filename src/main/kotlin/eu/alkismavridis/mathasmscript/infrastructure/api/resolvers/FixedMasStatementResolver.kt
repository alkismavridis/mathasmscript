package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.entities.logic.FixedMasStatement
import eu.alkismavridis.mathasmscript.usecases.names.getSimpleNameOf
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class FixedMasStatementResolver : GraphQLResolver<FixedMasStatement> {
    fun name(stmt: FixedMasStatement) : String {
        return getSimpleNameOf(stmt.path)
    }
}
