package eu.alkismavridis.mathasmscript.entities.repo


interface TheoryRepository {
    fun findAll() : List<Theory>
}
