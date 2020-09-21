package eu.alkismavridis.mathasmscript.usecases.parser.validate_dir_name

import eu.alkismavridis.mathasmscript.model.logic.MathAsmException
import java.util.regex.Pattern


val VALID_DIR_NAME_PATTERN = Pattern.compile("^/[a-zA-Z0-9/]+\$")

fun validateDirName(dirName:String) {
    if (!dirName.endsWith('/')) throw MathAsmException("Path must end with /")
    if (dirName.contains("//")) throw MathAsmException("Path may not contain empty directories, but found //")
    if (!VALID_DIR_NAME_PATTERN.matcher(dirName).find()) {
        throw MathAsmException("Only letters and numbers are allowed for directory names")
    }
}
