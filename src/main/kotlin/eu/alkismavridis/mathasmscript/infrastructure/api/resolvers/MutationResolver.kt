package eu.alkismavridis.mathasmscript.infrastructure.api.resolvers

import eu.alkismavridis.mathasmscript.infrastructure.persistence.*
import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.logic.StatementType
import eu.alkismavridis.mathasmscript.model.parser.ParseResult
import eu.alkismavridis.mathasmscript.model.repo.FixedMasStatement
import eu.alkismavridis.mathasmscript.usecases.parser.get_parent_package.GetParentPackage
import eu.alkismavridis.mathasmscript.usecases.parser.get_simple_name.GetSimpleName
import eu.alkismavridis.mathasmscript.usecases.parser.validate_package_part_name.ValidatePackagePartName
import eu.alkismavridis.mathasmscript.usecases.parser.validate_theorem_name.ValidateStatementName
import eu.alkismavridis.mathasmscript.usecases.repo.import_script.ImportScript
import eu.alkismavridis.mathasmscript.usecases.repo.move_statement.MoveStatement
import eu.alkismavridis.mathasmscript.usecases.repo.save_package.GetOrCreatePackage
import graphql.kickstart.tools.GraphQLMutationResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class MutationResolver(
        private val packageRepo: DbPackageRepository,
        private val stmtRepo: DbStatementRepository,
        private val scriptRepo: DbScriptRepository
) : GraphQLMutationResolver {

    companion object {
        val log = LoggerFactory.getLogger(MutationResolver::class.java)
    }

    fun commit(script: String): ParseResult {
        return ImportScript(this.stmtRepo, this.packageRepo, this.scriptRepo).run(script)
    }

    fun mvPackage(currentPath:String, newPath:String) {
        //1. Update package with new path + parentId. BE CAREFUL OF circular trees. Our new parent should not be a current child!
        //2. Update all affected full paths:
        // UPDATE PACKAGE SET PATH = CONCAT('booooo', SUBSTRING(PATH, LENGTH('bool.'), LENGTH(PATH))) WHERE PATH LIKE 'bool.%'

        //3. Update all affected statements (sql similar to the one above)
    }

    fun mvStatement(currentPath:String, newPath:String) : FixedMasStatement {
        return MoveStatement.move(currentPath, newPath, this.stmtRepo, this.packageRepo)
    }
}
