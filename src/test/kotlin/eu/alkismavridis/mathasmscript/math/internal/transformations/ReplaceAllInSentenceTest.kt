package eu.alkismavridis.mathasmscript.math.internal.transformations

import eu.alkismavridis.mathasmscript.math.StatementSide
import eu.alkismavridis.mathasmscript.math.StatementType
import eu.alkismavridis.mathasmscript.math.internal.model.LogicSelection
import eu.alkismavridis.mathasmscript.math.internal.utils.IllegalBaseException
import eu.alkismavridis.mathasmscript.testhelpers.Fixtures
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test


internal class ReplaceAllInSentenceTest {
    /// legitimate case tests
    @Test
    fun replaceAllInSentence_shouldPerformLegalReplacementOnLeftSentence() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.LEFT, base, LogicSelection(5), "cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 9, 3, 9, 4, 9, 3, 9, 9, 3, 9, 6)
        assertThat(result.left.length).isEqualTo(13)

        assertThat(result.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(result.right.length).isEqualTo(8)
    }

    @Test
    fun replaceAllInSentence_shouldPerformLegalReplacementOnRightSentence() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.RIGHT, base, LogicSelection(5),"cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 9, 3, 9, 9, 3, 9, 9, 3, 9)
        assertThat(result.right.length).isEqualTo(10)
    }


    @Test
    fun replaceAllInSentence_shouldAcceptLowerGradeBase() {
        val target = Fixtures.createTarget(true, 2, StatementType.OPEN_THEOREM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.RIGHT, base, LogicSelection(5),"cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)
    }

    @Test
    fun replaceAllInSentence_shouldAcceptEqualGradeBase() {
        val target = Fixtures.createTarget(true, 2, StatementType.OPEN_THEOREM)
        val base = Fixtures.createReverseBase(true, 2, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.RIGHT, base, LogicSelection(5),"cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)
    }

    @Test
    fun replaceAllInSentence_shouldEditRightSideOfUnidirectionalBase() {
        val target = Fixtures.createTarget(false, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.RIGHT, base, LogicSelection(5),"cloneName")

        assertThat(result).isSameAs(target)
        assertThat(result.name).isEqualTo("someTarget")
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)
    }

    @Test
    fun replaceAllInSentence_shouldCloneAxiom() {
        val target = Fixtures.createTarget(false, 0, StatementType.AXIOM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.RIGHT, base, LogicSelection(5),"cloneName")

        assertThat(result).isNotSameAs(target)
        assertThat(result.name).isEqualTo("cloneName")
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)


        //original should not be affected
        assertThat(target.name).isEqualTo("someTarget")
        assertThat(target.type).isEqualTo(StatementType.AXIOM)
        assertThat(target.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(target.left.length).isEqualTo(11)

        assertThat(target.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(target.right.length).isEqualTo(8)
    }

    @Test
    fun replaceAllInSentence_shouldCloneTheorem() {
        val target = Fixtures.createTarget(false, 0, StatementType.THEOREM)
        val base = Fixtures.createReverseBase(true, 0, StatementType.AXIOM)
        val result = replaceAllInSentence(target, StatementSide.RIGHT, base, LogicSelection(5),"cloneName")

        assertThat(result).isNotSameAs(target)
        assertThat(result.name).isEqualTo("cloneName")
        assertThat(result.type).isEqualTo(StatementType.OPEN_THEOREM)
        assertThat(result.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(result.left.length).isEqualTo(11)

        assertThat(result.right.words).startsWith(1, 1, 4, 1, 4, 1, 4)
        assertThat(result.right.length).isEqualTo(7)


        //original should not be affected
        assertThat(target.name).isEqualTo("someTarget")
        assertThat(target.type).isEqualTo(StatementType.THEOREM)
        assertThat(target.left.words).startsWith(5, 4, 1, 4, 4, 9, 3, 9, 1, 4, 6)
        assertThat(target.left.length).isEqualTo(11)

        assertThat(target.right.words).startsWith(1, 1, 4, 9, 3, 9, 1, 4)
        assertThat(target.right.length).isEqualTo(8)
    }


    /// handling of illegal cases
    @Test
    fun replaceAllInSentence_shouldRejectIllegalBase() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(false, 0, StatementType.HYPOTHESIS)
        assertThatThrownBy{ replaceAllInSentence(target, StatementSide.LEFT, base, LogicSelection(5),"cloneName") }
                .isExactlyInstanceOf(IllegalBaseException::class.java)
                .hasMessage("Statement of type HYPOTHESIS cannot be used as a base")
    }

    @Test
    fun replaceAllInSentence_shouldRejectBaseWithLargerGrade() {
        val target = Fixtures.createTarget(true, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(true, 2, StatementType.AXIOM)
        assertThatThrownBy{ replaceAllInSentence(target, StatementSide.LEFT, base, LogicSelection(5),"cloneName") }
                .isExactlyInstanceOf(IllegalSentenceReplacementException::class.java)
                .hasMessage("Cannot perform sentence-replacement because base \"someBase\" has larger grade than the target \"someTarget\"")
    }

    @Test
    fun replaceAllInSentence_shouldNotEditLeftSideOfUnidirectionalTarget() {
        val target = Fixtures.createTarget(false, 0, StatementType.OPEN_THEOREM)
        val base = Fixtures.createBase(true, 0, StatementType.AXIOM)
        assertThatThrownBy{ replaceAllInSentence(target, StatementSide.LEFT, base, LogicSelection(5),"cloneName") }
                .isExactlyInstanceOf(IllegalSentenceReplacementException::class.java)
                .hasMessage("Cannot edit left sentence of unidirectional target someTarget")
    }
}
