package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.parser.model.ParserResult
import eu.alkismavridis.mathasmscript.parser.usecases.ExecuteScript
import eu.alkismavridis.mathasmscript.theory.model.FixedMasStatement
import eu.alkismavridis.mathasmscript.theory.model.MasScript
import eu.alkismavridis.mathasmscript.theory.model.PackageContent
import eu.alkismavridis.mathasmscript.theory.model.Theory
import eu.alkismavridis.mathasmscript.theory.repo.PackageRepository
import eu.alkismavridis.mathasmscript.theory.repo.ScriptRepository
import eu.alkismavridis.mathasmscript.theory.repo.StatementRepository
import eu.alkismavridis.mathasmscript.theory.repo.TheoryRepository
import eu.alkismavridis.mathasmscript.theory.usecases.getPackageContent
import eu.alkismavridis.mathasmscript.theory.usecases.getPackageName
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
        val parentName = getPackageName(packageName)
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
