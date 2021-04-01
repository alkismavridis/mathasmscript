package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.infrastructure.persistence.*
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.MathAsmException
import eu.alkismavridis.mathasmscript.entities.parser.result.ParserResult
import eu.alkismavridis.mathasmscript.entities.repo.MasPackage
import eu.alkismavridis.mathasmscript.usecases.parser.createPackageUseCase
import eu.alkismavridis.mathasmscript.usecases.repo.ImportScript
import graphql.kickstart.tools.GraphQLMutationResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class MutationResolver(
        private val packageRepo: DbPackageRepository,
        private val stmtRepo: DbStatementRepository,
        private val scriptRepo: DbScriptRepository
) : GraphQLMutationResolver {
    fun commit(theoryId: Long, script: String): ParserResult {
        return ImportScript(theoryId, this.stmtRepo, this.packageRepo, this.scriptRepo).run(script)
    }

    fun mkdir(name: String, parentPath: String, theoryId: Long) : MasPackage {
        return createPackageUseCase(parentPath, name, theoryId, this.packageRepo)
    }

    fun rmdir(path: String, theoryId: Long) : Boolean {
        if (path.isEmpty()) {
            throw MathAsmException("Cannot delete root package")
        }

        val hasChildren = this.stmtRepo.existsByParent(path, theoryId) ||
                this.packageRepo.existsByParent(path, theoryId)

        if (hasChildren) {
            throw MathAsmException("Only empty directories can be deleted")
        }

        val wasDeleted = this.packageRepo.delete(path, theoryId)
        if (!wasDeleted) {
            throw MathAsmException("Package $path was not found int theory $theoryId")
        }

        return true
    }


    companion object {
        private val log = LoggerFactory.getLogger(MutationResolver::class.java)
    }
}
