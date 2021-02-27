package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.infrastructure.persistence.*
import eu.alkismavridis.mathasmscript.entities.parser.ParseResult
import eu.alkismavridis.mathasmscript.entities.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.entities.repo.MasPackage
import eu.alkismavridis.mathasmscript.usecases.parser.createPackageUseCase
import eu.alkismavridis.mathasmscript.usecases.repo.ImportScript
import eu.alkismavridis.mathasmscript.usecases.repo.moveStatement
import graphql.kickstart.tools.GraphQLMutationResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class MutationResolver(
        private val packageRepo: DbPackageRepository,
        private val stmtRepo: DbStatementRepository,
        private val scriptRepo: DbScriptRepository
) : GraphQLMutationResolver {
    fun commit(theoryId: Long, script: String): ParseResult {
        return ImportScript(theoryId, this.stmtRepo, this.packageRepo, this.scriptRepo).run(script)
    }

    fun createPackage(name: String, parentPath: String, theoryId: Long) : MasPackage {
        return createPackageUseCase(parentPath, name, theoryId, this.packageRepo)
    }

    fun mvPackage(theoryId: Long, currentPath:String, newPath:String) {
        //1. Update package with new path + parentId. BE CAREFUL OF circular trees. Our new parent should not be a current child!
        //2. Update all affected full paths:
        // UPDATE PACKAGE SET PATH = CONCAT('booooo', SUBSTRING(PATH, LENGTH('bool.'), LENGTH(PATH))) WHERE PATH LIKE 'bool.%'

        //3. Update all affected statements (sql similar to the one above)
    }

    fun mvStatement(theoryId: Long, currentPath:String, newPath:String) : FixedMasStatement {
        return moveStatement(theoryId, currentPath, newPath, this.stmtRepo, this.packageRepo)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MutationResolver::class.java)
    }
}
