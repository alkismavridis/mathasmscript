package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.model.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.usecases.parser.get_simple_name.GetSimpleName
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class FixedMasStatementResolver : GraphQLResolver<FixedMasStatement> {
    fun name(stmt: FixedMasStatement) : String {
        return GetSimpleName.get(stmt.path)
    }
}
