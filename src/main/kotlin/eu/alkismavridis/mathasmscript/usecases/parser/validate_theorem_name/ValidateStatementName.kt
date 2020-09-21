package eu.alkismavridis.mathasmscript.usecases.parser.validate_theorem_name

import java.util.regex.Pattern


class ValidateStatementName {
    companion object {
        private val VALID_THEOREM_NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$")
        private val VALID_AXIOM_NAME_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$")

        const val INVALID_AXIOM_MESSAGE = "Axioms should contain only capital letters, numbers and underscores, starting with a capital letter"
        const val INVALID_THEOREM_MESSAGE = "Theorems should contain only letters and numbers, starting with a capital letter"

        fun isTheoremNameValid(name:String) : Boolean {
            return VALID_THEOREM_NAME_PATTERN.matcher(name).find()
        }

        fun isAxiomNameValid(name:String) : Boolean {
            return VALID_AXIOM_NAME_PATTERN.matcher(name).find()
        }
    }
}
