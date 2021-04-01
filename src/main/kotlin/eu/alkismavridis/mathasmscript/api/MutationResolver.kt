package eu.alkismavridis.mathasmscript.api

import eu.alkismavridis.mathasmscript.parser.ImportScript
import eu.alkismavridis.mathasmscript.parser.result.ParserResult
import eu.alkismavridis.mathasmscript.repo.MasPackage
import eu.alkismavridis.mathasmscript.repo.PackageRepository
import eu.alkismavridis.mathasmscript.repo.ScriptRepository
import eu.alkismavridis.mathasmscript.repo.StatementRepository
import eu.alkismavridis.mathasmscript.repo.usecases.createPackageUseCase
import graphql.kickstart.tools.GraphQLMutationResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class MutationResolver(
        private val packageRepo: PackageRepository,
        private val stmtRepo: StatementRepository,
        private val scriptRepo: ScriptRepository
) : GraphQLMutationResolver {
    fun commit(theoryId: Long, script: String): ParserResult {
        return ImportScript(theoryId, this.stmtRepo, this.packageRepo, this.scriptRepo).run(script)
    }

    fun mkdir(name: String, parentPath: String, theoryId: Long) : MasPackage {
        return createPackageUseCase(parentPath, name, theoryId, this.packageRepo)
    }

    fun rmdir(path: String, theoryId: Long) : Boolean {
        if (path.isEmpty()) {
            throw IllegalArgumentException("Cannot delete root package")
        }

        val hasChildren = this.stmtRepo.existsByParent(path, theoryId) ||
                this.packageRepo.existsByParent(path, theoryId)

        if (hasChildren) {
            throw IllegalArgumentException("Only empty directories can be deleted")
        }

        val wasDeleted = this.packageRepo.delete(path, theoryId)
        if (!wasDeleted) {
            throw IllegalArgumentException("Package $path was not found int theory $theoryId")
        }

        return true
    }


    companion object {
        private val log = LoggerFactory.getLogger(MutationResolver::class.java)
    }
}
