package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbPackageRepository
import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbScriptRepository
import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbStatementRepository
import eu.alkismavridis.mathasmscript.model.parser.ParseResult
import eu.alkismavridis.mathasmscript.model.repo.MasScript
import eu.alkismavridis.mathasmscript.model.repo.PackageContent
import eu.alkismavridis.mathasmscript.usecases.parser.execute_script.ExecuteScript
import eu.alkismavridis.mathasmscript.usecases.repo.get_package_contents.GetPackageContents
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
        return GetPackageContents.get(packageName, this.packageRepo, this.stmtRepo)
    }

    fun cat(file: String): MasScript? {
        return this.scriptRepo.find(file)
    }
}
