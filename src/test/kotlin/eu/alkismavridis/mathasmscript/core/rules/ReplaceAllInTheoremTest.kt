package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.LogicSelection
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.internal.IllegalBaseException
import eu.alkismavridis.mathasmscript.core.internal.IllegalReplaceAllException
import eu.alkismavridis.mathasmscript.testhelpers.Fixtures
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ReplaceAllInTheoremTest {
    @Test
    fun replaceAll_shouldPerformValidReplacement() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceAll(target, base, LogicSelection(5), "cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 9, 3, 9, 4, 9, 3, 9, 9, 3, 9, 6)
        assertThat(result.left.length).isEqualTo(13)

        assertThat(result.right.words).startsWith(1, 9, 3, 9, 9, 3, 9, 9, 3, 9)
        assertThat(result.right.length).isEqualTo(10)
    }

    @Test
    fun replaceAll_shouldAllowLargeGradeBaseForUnidirectionalTarget() {
        val target = Fixtures.createTarget(false, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(true, 5, StatementType.AXIOM)
        val result = replaceAll(target, base, LogicSelection(5), "cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 9, 3, 9, 4, 9, 3, 9, 9, 3, 9, 6)
        assertThat(result.left.length).isEqualTo(13)

        assertThat(result.right.words).startsWith(1, 9, 3, 9, 9, 3, 9, 9, 3, 9)
        assertThat(result.right.length).isEqualTo(10)
    }

    @Test
    fun replaceAll_shouldCloneAxiom() {
        val target = Fixtures.createTarget(true, 0, StatementType.AXIOM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceAll(target, base, LogicSelection(5), "cloneName")

        assertThat(result).isNotSameAs(target)
        assertThat(result.name).isEqualTo("cloneName")
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
        assertThat(result.left.words).startsWith(5, 4, 9, 3, 9, 4, 9, 3, 9, 9, 3, 9, 6)
        assertThat(result.left.length).isEqualTo(13)

        assertThat(result.right.words).startsWith(1, 9, 3, 9, 9, 3, 9, 9, 3, 9)
        assertThat(result.right.length).isEqualTo(10)

        // original should not be edited
        assertThat(target.name).isEqualTo("someTarget")
        assertThat(target.type).isEqualTo(StatementType.AXIOM)
        assertThat(target.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(target.left.length).isEqualTo(11)

        assertThat(target.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(target.right.length).isEqualTo(8)
    }

    @Test
    fun replaceAll_shouldCloneTheorem() {
        val target = Fixtures.createTarget(true, 0, StatementType.THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceAll(target, base, LogicSelection(5), "cloneName")

        assertThat(result).isNotSameAs(target)
        assertThat(result.name).isEqualTo("cloneName")
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
        assertThat(result.left.words).startsWith(5, 4, 9, 3, 9, 4, 9, 3, 9, 9, 3, 9, 6)
        assertThat(result.left.length).isEqualTo(13)

        assertThat(result.right.words).startsWith(1, 9, 3, 9, 9, 3, 9, 9, 3, 9)
        assertThat(result.right.length).isEqualTo(10)

        // original should not be edited
        assertThat(target.name).isEqualTo("someTarget")
        assertThat(target.type).isEqualTo(StatementType.THEOREM)
        assertThat(target.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(target.left.length).isEqualTo(11)

        assertThat(target.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(target.right.length).isEqualTo(8)
    }


    /// Erro handling
    @Test
    fun replaceAll_shouldRejectInvalidBase() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.HYPOTHESIS)
        assertThatThrownBy{ replaceAll(target, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalBaseException::class.java)
                .hasMessage("Statement of type HYPOTHESIS cannot be used as a base")
    }

    @Test
    fun replaceAll_shouldRejectLowGradeBaseForUnidirectionalTarget() {
        val target = Fixtures.createTarget(false, 2, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(true, 1, StatementType.AXIOM)

        assertThatThrownBy{ replaceAll(target, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalReplaceAllException::class.java)
                .hasMessage("Cannot use someBase as a base to replace-all. Base's grade should ba larger than unidirectional target's. Found: base 1 - 2 target")
    }

    @Test
    fun replaceAll_shouldRejectEqualGradeBaseForUnidirectionalTarget() {
        val target = Fixtures.createTarget(false, 1, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(true, 1, StatementType.AXIOM)

        assertThatThrownBy{ replaceAll(target, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalReplaceAllException::class.java)
                .hasMessage("Cannot use someBase as a base to replace-all. Base's grade should ba larger than unidirectional target's. Found: base 1 - 1 target")
    }
}
