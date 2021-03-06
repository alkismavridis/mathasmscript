package eu.alkismavridis.mathasmscript.usecases.logic.start_theorem

import eu.alkismavridis.mathasmscript.Fixtures
import eu.alkismavridis.mathasmscript.model.logic.StatementSide
import eu.alkismavridis.mathasmscript.model.logic.StatementType
import eu.alkismavridis.mathasmscript.usecases.logic.assert_base_legality.IllegalBaseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class StartTheoremTest {
    /// successful casses
    @Test
    fun startTheorem_shouldCloneValidBase() {
        val base = Fixtures.createBase(true, 4, StatementType.THEOREM)
        base.left.ensureCapacity(100)
        base.right.ensureCapacity(100)

        val result = startTheorem("myOpenTheorem", base, StatementSide.BOTH)
        assertThat(result.name).isEqualTo("myOpenTheorem")
        assertThat(result.left.words).startsWith(1, 4)
        assertThat(result.left.length).isEqualTo(2)
        assertThat(result.right.words).startsWith(9, 3, 9)
        assertThat(result.right.length).isEqualTo(3)
        assertThat(result.grade).isEqualTo(4)
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
    }


    @Test
    fun startTheorem_shouldCloneValidLeftSide() {
        val base = Fixtures.createBase(true, 4, StatementType.THEOREM)
        base.left.ensureCapacity(100)
        base.right.ensureCapacity(100)

        val result = startTheorem("myOpenTheorem", base, StatementSide.LEFT)
        assertThat(result.name).isEqualTo("myOpenTheorem")
        assertThat(result.left.words).startsWith(1, 4)
        assertThat(result.left.length).isEqualTo(2)
        assertThat(result.right.words).startsWith(1, 4)
        assertThat(result.right.length).isEqualTo(2)
        assertThat(result.grade).isEqualTo(4)
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
    }

    @Test
    fun startTheorem_shouldCloneValidRightSide() {
        val base = Fixtures.createBase(true, 4, StatementType.THEOREM)
        base.left.ensureCapacity(100)
        base.right.ensureCapacity(100)

        val result = startTheorem("myOpenTheorem", base, StatementSide.RIGHT)
        assertThat(result.name).isEqualTo("myOpenTheorem")
        assertThat(result.left.words).startsWith(9, 3, 9)
        assertThat(result.left.length).isEqualTo(3)
        assertThat(result.right.words).startsWith(9, 3, 9)
        assertThat(result.right.length).isEqualTo(3)
        assertThat(result.grade).isEqualTo(4)
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
    }


    /// error handling
    @Test
    fun startTheorem_shouldRejectIllegalBase() {
        val base = Fixtures.createBase(true, 4, StatementType.HYPOTHESIS)
        assertThatThrownBy{ startTheorem("myOpenTheorem", base, StatementSide.RIGHT) }
                .isExactlyInstanceOf(IllegalBaseException::class.java)
                .hasMessage("Statement of type HYPOTHESIS cannot be used as a base")
    }

    @Test
    fun startTheorem_shouldRejectIllegalRightSideCloning() {
        val base = Fixtures.createBase(false, 4, StatementType.AXIOM)
        assertThatThrownBy{ startTheorem("myOpenTheorem", base, StatementSide.RIGHT) }
                .isExactlyInstanceOf(IllegalTheoremStartException::class.java)
                .hasMessage("Cannot clone right side of unidirectional base someBase")
    }
}
