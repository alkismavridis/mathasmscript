package eu.alkismavridis.mathasmscript.theory.repo

import eu.alkismavridis.mathasmscript.theory.model.Theory


interface TheoryRepository {
    fun findAll() : List<Theory>
}
