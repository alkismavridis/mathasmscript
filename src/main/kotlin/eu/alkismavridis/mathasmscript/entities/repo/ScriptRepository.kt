package eu.alkismavridis.mathasmscript.entities.repo

import eu.alkismavridis.mathasmscript.entities.parser.result.MasVariable

interface ScriptRepository {
    fun find(scriptName: String, theoryId: Long): MasScript?
    fun saveScript(script: MasScript, imports: Collection<MasVariable>)
}
