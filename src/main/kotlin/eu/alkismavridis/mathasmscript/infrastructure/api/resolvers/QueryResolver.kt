package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbPackageRepository
import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbScriptRepository
import eu.alkismavridis.mathasmscript.infrastructure.persistence.DbStatementRepository
import eu.alkismavridis.mathasmscript.entities.parser.ParseResult
import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement
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
    fun execute(theoryId: Long, script: String): ParseResult {
        return ExecuteScript(theoryId, this.stmtRepo).run(script)
    }

    fun ls(theoryId: Long, packageName: String): PackageContent {
        return getPackageContent(theoryId, packageName, this.packageRepo, this.stmtRepo)
    }

    fun cat(theoryId: Long, file: String): MasScript? {
        return this.scriptRepo.find(file, theoryId)
    }

    fun dependencies(statementId: Long): List<FixedMasStatement> {
        return this.stmtRepo.findDependenciesOf(statementId)
    }
}
