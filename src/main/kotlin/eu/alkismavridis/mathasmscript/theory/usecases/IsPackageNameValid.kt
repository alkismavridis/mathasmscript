package eu.alkismavridis.mathasmscript.theory.validations

import java.util.regex.Pattern


private val VALID_PART_NAME_PATTERN: Pattern = Pattern.compile("^[a-z][a-z0-9_]*\$")
const val PACKAGE_NAME_ERROR_MESSAGE = "Package names must start with lower case letter and contain only lower-case letters, numbers or underscores"


fun isPackageNameValid(partName:String) : Boolean {
    return VALID_PART_NAME_PATTERN.matcher(partName).find()
}

