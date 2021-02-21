package eu.alkismavridis.mathasmscript.entities.repo

interface ScriptRepository {
    fun find(scriptName: String, theoryId: Long): MasScript?
    fun saveScript(script: MasScript, importIds:List<ImportId>)
}
