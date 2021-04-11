package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.theory.model.FixedMasStatement
import eu.alkismavridis.mathasmscript.theory.usecases.getSimpleName
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class FixedMasStatementResolver : GraphQLResolver<FixedMasStatement> {
    fun name(stmt: FixedMasStatement) : String {
        return getSimpleName(stmt.path)
    }
}
