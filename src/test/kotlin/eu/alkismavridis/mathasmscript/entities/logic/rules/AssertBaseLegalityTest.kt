package eu.alkismavridis.mathasmscript.entities.logic.rules

import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.IllegalBaseException
import eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality.assertBaseLegality
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AssertBaseLegalityTest {
    @Test
    fun assertBaseLegality_shouldAllowAxiom() {
        assertThatCode{ assertBaseLegality(StatementType.AXIOM) }.doesNotThrowAnyException()
    }

    @Test
    fun assertBaseLegality_shouldAllowTheorem() {
        assertThatCode{ assertBaseLegality(StatementType.THEOREM) }.doesNotThrowAnyException()
    }

    @Test
    fun assertBaseLegality_shouldAllowOpenAxiom() {
        assertThatCode{ assertBaseLegality(StatementType.OPEN_THEOREM) }.doesNotThrowAnyException()
    }

    @Test
    fun assertBaseLegality_shouldThrowForHypothesis() {
        assertThatThrownBy{ assertBaseLegality(StatementType.HYPOTHESIS) }
                .isExactlyInstanceOf(IllegalBaseException::class.java)
                .hasMessage("Statement of type HYPOTHESIS cannot be used as a base")
    }
}
