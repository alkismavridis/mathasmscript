package eu.alkismavridis.mathasmscript.math

enum class StatementType(val canUseAsBase:Boolean, val hasProof:Boolean, val canChange:Boolean) {
    AXIOM(true, false, false),
    THEOREM(true, true, false),
    OPEN_THEOREM(true, true, true), //a not-yet-saved theorem
    HYPOTHESIS(false, false, false),
    ;
}
