package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.parser.ExecuteScript
import eu.alkismavridis.mathasmscript.parser.result.ParserResult
import eu.alkismavridis.mathasmscript.repo.*
import eu.alkismavridis.mathasmscript.repo.names.getPackageNameOf
import eu.alkismavridis.mathasmscript.repo.usecases.getPackageContent
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private val packageRepo: PackageRepository,
        private val stmtRepo: StatementRepository,
        private val scriptRepo: ScriptRepository,
        private val theoryRepo: TheoryRepository
) : GraphQLQueryResolver {
    fun execute(theoryId: Long, script: String): ParserResult {
        return ExecuteScript(theoryId, this.stmtRepo).run(script)
    }

    fun ls(theoryId: Long, packageName: String): PackageContent {
        return getPackageContent(theoryId, packageName, this.packageRepo, this.stmtRepo)
    }

    fun lsParent(theoryId: Long, packageName: String): PackageContent {
        val parentName = getPackageNameOf(packageName)
        return getPackageContent(theoryId, parentName, this.packageRepo, this.stmtRepo)
    }

    fun cat(theoryId: Long, file: String): MasScript? {
        return this.scriptRepo.find(file, theoryId)
    }

    fun dependencies(statementId: Long): List<FixedMasStatement> {
        return this.stmtRepo.findDependenciesOf(statementId)
    }

    fun theories(): List<Theory> {
        return this.theoryRepo.findAll()
    }
}
