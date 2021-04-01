package eu.alkismavridis.mathasmscript.repo

import eu.alkismavridis.mathasmscript.parser.result.MasVariable

interface ScriptRepository {
    fun find(scriptName: String, theoryId: Long): MasScript?
    fun saveScript(script: MasScript, imports: Collection<MasVariable>)
}
