package eu.alkismavridis.mathasmscript.entities.repo

import eu.alkismavridis.mathasmscript.entities.parser.result.MasImport

interface ScriptRepository {
    fun find(scriptName: String, theoryId: Long): MasScript?
    fun saveScript(script: MasScript, importIds: Collection<MasImport>)
}
