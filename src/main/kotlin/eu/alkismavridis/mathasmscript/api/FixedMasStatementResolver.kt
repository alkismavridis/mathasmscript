package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.repo.names.getSimpleNameOf
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class FixedMasStatementResolver : GraphQLResolver<FixedMasStatement> {
    fun name(stmt: FixedMasStatement) : String {
        return getSimpleNameOf(stmt.path)
    }
}