package eu.alkismavridis.mathasmscript.repo.names.validations

import eu.alkismavridis.mathasmscript.core.exceptions.MathAsmException
import java.util.regex.Pattern


private val VALID_PART_NAME_PATTERN: Pattern = Pattern.compile("^[a-z][a-z0-9_]*\$")
const val PACKAGE_NAME_ERROR_MESSAGE = "Package names must start with lower case letter and contain only lower-case letters, numbers or underscores"


fun isPackageNameValid(partName:String) : Boolean {
    return VALID_PART_NAME_PATTERN.matcher(partName).find()
}

fun assertPackageNameValid(partName:String) {
    if (!isPackageNameValid(partName)) {
        throw MathAsmException(PACKAGE_NAME_ERROR_MESSAGE)
    }
}

fun isPackagePathValid(path: String) : Boolean {
    return path.split(".").none{ !isPackageNameValid(it) }

}

fun assertPackagePathValid(path: String) {
    if (!isPackagePathValid(path)) {
        throw MathAsmException(PACKAGE_NAME_ERROR_MESSAGE)
    }
}

