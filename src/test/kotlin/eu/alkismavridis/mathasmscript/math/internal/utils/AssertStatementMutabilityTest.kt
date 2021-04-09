package eu.alkismavridis.mathasmscript.math.internal.utils

import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.MathAsmException
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
                .isExactlyInstanceOf(MathAsmException::class.java)
                .hasMessage("Statement of type AXIOM is immutable")
    }

    @Test
    fun assertStatementMutability_shouldNotAllowTheorem() {
        assertThatThrownBy{ assertStatementMutability(StatementType.THEOREM) }
                .isExactlyInstanceOf(MathAsmException::class.java)
                .hasMessage("Statement of type THEOREM is immutable")
    }

    @Test
    fun assertStatementMutability_shouldNotAllowHypothesis() {
        assertThatThrownBy{ assertStatementMutability(StatementType.HYPOTHESIS) }
                .isExactlyInstanceOf(MathAsmException::class.java)
                .hasMessage("Statement of type HYPOTHESIS is immutable")
    }
}
