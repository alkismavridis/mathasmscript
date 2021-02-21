package eu.alkismavridis.mathasmscript.entities.logic.rules

import eu.alkismavridis.mathasmscript.entities.logic.StatementType
import eu.alkismavridis.mathasmscript.entities.logic.exceptions.StatementImmutableException
import eu.alkismavridis.mathasmscript.usecases.logic.assert_statement_mutable.assertStatementMutability
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AssertStatementMutabilityTest {
    @Test
    fun assertStatementMutability_shouldNotAllowOpenTheorem() {
        assertThatCode{ assertStatementMutability(StatementType.OPEN_THEOREM) }.doesNotThrowAnyException()
    }

    @Test
    fun assertStatementMutability_shouldNotAllowAxiom() {
        assertThatThrownBy{ assertStatementMutability(StatementType.AXIOM) }
                .isExactlyInstanceOf(StatementImmutableException::class.java)
                .hasMessage("Statement of type AXIOM is immutable")
    }

    @Test
    fun assertStatementMutability_shouldNotAllowTheorem() {
        assertThatThrownBy{ assertStatementMutability(StatementType.THEOREM) }
                .isExactlyInstanceOf(StatementImmutableException::class.java)
                .hasMessage("Statement of type THEOREM is immutable")
    }

    @Test
    fun assertStatementMutability_shouldNotAllowHypothesis() {
        assertThatThrownBy{ assertStatementMutability(StatementType.HYPOTHESIS) }
                .isExactlyInstanceOf(StatementImmutableException::class.java)
                .hasMessage("Statement of type HYPOTHESIS is immutable")
    }
}
