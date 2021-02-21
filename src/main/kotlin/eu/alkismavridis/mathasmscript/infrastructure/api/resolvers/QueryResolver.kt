package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbPackageRepository
import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbScriptRepository
import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbStatementRepository
import eu.alkismavridis.mathasmscript.entities.parser.ParseResult
import eu.alkismavridis.mathasmscript.entities.repo.MasScript
import eu.alkismavridis.mathasmscript.entities.repo.PackageContent
import eu.alkismavridis.mathasmscript.usecases.parser.execute_script.ExecuteScript
import eu.alkismavridis.mathasmscript.usecases.repo.getPackageContent
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private val packageRepo: DbPackageRepository,
        private val stmtRepo: DbStatementRepository,
        private val scriptRepo: DbScriptRepository
) : GraphQLQueryResolver {
    fun execute(script: String): ParseResult {
        return ExecuteScript(this.stmtRepo).run(script)
    }

    fun ls(packageName: String): PackageContent {
        return getPackageContent(packageName, this.packageRepo, this.stmtRepo)
    }

    fun cat(file: String): MasScript? {
        return this.scriptRepo.find(file)
    }
}
