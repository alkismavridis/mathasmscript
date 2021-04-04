package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.internal.StatementImmutableException
import eu.alkismavridis.mathasmscript.core.internal.assertStatementMutability
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
