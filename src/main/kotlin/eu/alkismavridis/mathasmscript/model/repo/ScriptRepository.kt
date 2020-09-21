package eu.alkismavridis.mathasmscript.model.repo

import eu.alkismavridis.mathasmscript.usecases.parser.parse_script.ImportId

interface ScriptRepository {
    fun saveScript(fileName:String, packageName:String, source:String, importIds: List<ImportId>)
}
