package eu.alkismavridis.mathasmscript.theory.repo

import eu.alkismavridis.mathasmscript.theory.model.MasScript
import eu.alkismavridis.mathasmscript.theory.model.MasScriptImport

interface ScriptRepository {
    fun find(scriptName: String, theoryId: Long): MasScript?
    fun saveScript(script: MasScript, imports: Collection<MasScriptImport>)
}
