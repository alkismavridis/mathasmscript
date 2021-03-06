package eu.alkismavridis.mathasmscript.usecases.repo.save_package

import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import eu.alkismavridis.mathasmscript.model.repo.MasPackage
import eu.alkismavridis.mathasmscript.model.repo.PackageRepository
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.stream.Collectors

class GetOrCreatePackage {
    companion object {
        val log = LoggerFactory.getLogger(GetOrCreatePackage::class.java)

        fun get(path:String, createdAt: Instant, repo:PackageRepository) : MasPackage {
            val parentNames = getParentNames(path)
            val existingParents = getPackageMap(parentNames, repo)

            for (i in parentNames.indices) {
                val path = parentNames[i]
                val parentPath:String? = if(i==0) null else parentNames[i-1]

                val existingPackage = existingParents[ path ]
                val existingParent = if(parentPath == null) null else existingParents[ parentPath ]

                if (existingPackage != null) continue

                log.info("Creating package {}", path)
                val newPackage = MasPackage(path, existingParent?.id, createdAt, -1L)
                repo.saveAll(listOf(newPackage))
                existingParents[ newPackage.path ] = newPackage
                log.info("Package {} was created with id {}", newPackage.path, newPackage.id)
            }

            return existingParents[path] ?: throw MathAsmException("Internal error: could not save package ${path}")
        }


        private fun getParentNames(path: String) : List<String> {
            val dotIndexes = mutableListOf(0)
            var index: Int = path.indexOf('.')
            while (index >= 0) {
                dotIndexes.add(index)
                index = path.indexOf('.', index + 1)
            }

            val parentNames = dotIndexes.stream()
                    .map{ path.substring(0, it) }
                    .collect(Collectors.toList())
            parentNames.add(path)

            return parentNames
        }

        private fun getPackageMap(paths:List<String>, repo:PackageRepository) : MutableMap<String, MasPackage> {
            val existingParents = repo.findExistingNames(paths)
            val result = mutableMapOf<String, MasPackage>()
            existingParents.forEach { result[it.path] = it }

            return result
        }
    }
}
