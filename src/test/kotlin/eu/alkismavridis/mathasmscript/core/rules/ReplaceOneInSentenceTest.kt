package eu.alkismavridis.mathasmscript.core.rules

import eu.alkismavridis.mathasmscript.core.LogicSelection
import eu.alkismavridis.mathasmscript.core.StatementSide
import eu.alkismavridis.mathasmscript.core.StatementType
import eu.alkismavridis.mathasmscript.core.internal.IllegalBaseException
import eu.alkismavridis.mathasmscript.core.internal.IllegalSingleReplacementException
import eu.alkismavridis.mathasmscript.testhelpers.Fixtures
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ReplaceOneInSentenceTest {
    /// successful case tests
    @Test
    fun replaceSingleMatch_shouldHandleValidReplacementInLeftSide() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceSingleMatch(target, StatementSide.LEFT, 2, base, LogicSelection(5), "cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 9, 3, 9, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(12)

        assertThat(result.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(result.right.length).isEqualTo(8)
    }

    @Test
    fun replaceSingleMatch_shouldHandleValidReplacementInRightSide() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceSingleMatch(target, StatementSide.RIGHT, 6, base, LogicSelection(5), "cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 9, 3, 9, 9, 3, 9)
        assertThat(result.right.length).isEqualTo(9)
    }

    @Test
    fun replaceSingleMatch_shouldAllowEditingRightSideOfUnidirectionalBase() {
        val target = Fixtures.createTarget(false, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceSingleMatch(target, StatementSide.RIGHT, 3, base, LogicSelection(5), "cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)
    }

    @Test
    fun replaceSingleMatch_shouldCloneAxiomTarget() {
        val target = Fixtures.createTarget(false, 0, StatementType.AXIOM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceSingleMatch(target, StatementSide.RIGHT, 3, base, LogicSelection(5), "cloneName")

        assertThat(result).isNotSameAs(target)
        assertThat(result.name).isEqualTo("cloneName")
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)

        //original should stay untouched
        assertThat(target.name).isEqualTo("someTarget")
        assertThat(target.type).isEqualTo(StatementType.AXIOM)
        assertThat(target.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(target.left.length).isEqualTo(11)

        assertThat(target.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(target.right.length).isEqualTo(8)
    }

    @Test
    fun replaceSingleMatch_shouldCloneTheoremTarget() {
        val target = Fixtures.createTarget(false, 0, StatementType.THEOREM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceSingleMatch(target, StatementSide.RIGHT, 3, base, LogicSelection(5), "cloneName")

        assertThat(result).isNotSameAs(target)
        assertThat(result.name).isEqualTo("cloneName")
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)

        //original should stay untouched
        assertThat(target.name).isEqualTo("someTarget")
        assertThat(target.type).isEqualTo(StatementType.THEOREM)
        assertThat(target.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(target.left.length).isEqualTo(11)

        assertThat(target.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(target.right.length).isEqualTo(8)
    }


    /// illegal case handling
    @Test
    fun replaceSingleMatch_shouldRejectIllegalBase() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.HYPOTHESIS)
        assertThatThrownBy{ replaceSingleMatch(target, StatementSide.LEFT, 2, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalBaseException::class.java)
                .hasMessage("Statement of type HYPOTHESIS cannot be used as a base")
    }

    @Test
    fun replaceSingleMatch_shouldRejectIllegalBaseGrade() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 1, StatementType.AXIOM)
        assertThatThrownBy{ replaceSingleMatch(target, StatementSide.LEFT, 2, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalSingleReplacementException::class.java)
                .hasMessage("Only zero-grade bases can be used for single replacement, but someBase had 1")
    }

    @Test
    fun replaceSingleMatch_shouldNotEditLeftSideOfUnidirectionalTarget() {
        val target = Fixtures.createTarget(false, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        assertThatThrownBy{ replaceSingleMatch(target, StatementSide.LEFT, 2, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalSingleReplacementException::class.java)
                .hasMessage("Cannot edit left side of unidirectional target someTarget")
    }

    @Test
    fun replaceSingleMatch_shouldNotEditLeftSideIfSymbolsDoNotMatch() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        assertThatThrownBy{ replaceSingleMatch(target, StatementSide.LEFT, 1, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalSingleReplacementException::class.java)
                .hasMessage("Symbols did not match with base someBase: someTarget -> left side -> position 1")
    }

    @Test
    fun replaceSingleMatch_shouldNotEditRightSideIfSymbolsDoNotMatch() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        assertThatThrownBy{ replaceSingleMatch(target, StatementSide.RIGHT, 3, base, LogicSelection(5), "cloneName") }
                .isExactlyInstanceOf(IllegalSingleReplacementException::class.java)
                .hasMessage("Symbols did not match with base someBase: someTarget -> right side -> position 3")
    }
}
