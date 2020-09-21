package eu.alkismavridis.mathasmscript.usecases.parser.validate_package_part_name

import java.util.regex.Pattern


class ValidatePackagePartName {
    companion object {
        private val VALID_PART_NAME_PATTERN: Pattern = Pattern.compile("^[a-z][a-z0-9_]*\$")
        const val ERROR_MESSAGE = "Package name must start with lower case letter and contain only lower-case letters, numbers or underscores"


        fun isPackagePartNameValid(partName:String) : Boolean {
            return VALID_PART_NAME_PATTERN.matcher(partName).find()
        }
    }
}

