package eu.alkismavridis.mathasmscript.repo

interface ScriptRepository {
    fun find(scriptName: String, theoryId: Long): MasScript?
    fun saveScript(script: MasScript, imports: Collection<MasScriptImport>)
}
