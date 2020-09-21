package eu.alkismavridis.mathasmscript.model.logic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MathAsmSentenceTest {

    /// select tests
    @Test
    fun select_shouldDetectEdges() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)

        val sel = SentenceSelection(5)
        target.select(searchTerm, sel)

        assertThat(sel.length).isEqualTo(2)
        assertThat(sel.positions[0]).isEqualTo(0)
        assertThat(sel.positions[1]).isEqualTo(3)
    }

    @Test
    fun select_shouldDetectMiddle() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(2,3), false)

        val sel = SentenceSelection(5)
        target.select(searchTerm, sel)

        assertThat(sel.length).isEqualTo(1)
        assertThat(sel.positions[0]).isEqualTo(1)
    }

    @Test
    fun select_shouldBeAbleToSelectItself() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val sel = SentenceSelection(5)
        target.select(target, sel)

        assertThat(sel.length).isEqualTo(1)
        assertThat(sel.positions[0]).isEqualTo(0)
    }


    /// replace tests
    @Test
    fun replace_shouldHandleEqualSizeReplacement() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)
        val replacement = MathAsmSentence(longArrayOf(9,8), false)


        val sel = SentenceSelection(5)
        target.select(searchTerm, sel)
        target.replace(replacement, searchTerm.length, sel)

        assertThat(target.words).containsExactly(9, 8, 3, 9, 8)
        assertThat(target.length).isEqualTo(5)
    }

    @Test
    fun replace_shouldHandleShorterReplacement() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)
        val replacement = MathAsmSentence(longArrayOf(9), false)

        val sel = SentenceSelection(5)
        target.select(searchTerm, sel)
        target.replace(replacement, searchTerm.length, sel)

        assertThat(target.length).isEqualTo(3)
        assertThat(target.words).startsWith(9, 3, 9)
    }

    @Test
    fun replace_shouldHandleLargerReplacement() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)
        val replacement = MathAsmSentence(longArrayOf(9,8,7,9), false)

        val sel = SentenceSelection(5)
        target.select(searchTerm, sel)
        target.replace(replacement, searchTerm.length, sel)

        assertThat(target.length).isEqualTo(9)
        assertThat(target.words).startsWith(9,8,7,9 ,3, 9,8,7,9)
    }

    @Test
    fun replace_shouldHandleLargerReplacementWithoutCopy() {
        val target = MathAsmSentence(longArrayOf(1,2,3,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)
        val replacement = MathAsmSentence(longArrayOf(9,8,7,9), false)

        target.ensureCapacity(500)
        assertThat(target.getCapacity()).isEqualTo(500)


        val sel = SentenceSelection(5)
        target.select(searchTerm, sel)
        target.replace(replacement, searchTerm.length, sel)

        assertThat(target.length).isEqualTo(9)
        assertThat(target.words).startsWith(9,8,7,9 ,3, 9,8,7,9)
    }


    /// match tests
    @Test
    fun match_shouldDetectPositives() {
        val target = MathAsmSentence(longArrayOf(1,2,1,3,1,2,2,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)

        assertThat(target.match(searchTerm, 0)).isTrue()
        assertThat(target.match(searchTerm, 4)).isTrue()
        assertThat(target.match(searchTerm, 7)).isTrue()
    }

    @Test
    fun match_shouldDetectSelf() {
        val target = MathAsmSentence(longArrayOf(1, 2, 1, 3, 1, 2, 2, 1, 2), false)
        assertThat(target.match(target, 0)).isTrue()
    }

    @Test
    fun match_shouldNotGiveFalsePositives() {
        val target = MathAsmSentence(longArrayOf(1,2,1,3,1,2,2,1,2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1,2), false)

        assertThat(target.match(searchTerm, 1)).isFalse()
        assertThat(target.match(searchTerm, 2)).isFalse()
        assertThat(target.match(searchTerm, 3)).isFalse()
        assertThat(target.match(searchTerm, 5)).isFalse()
        assertThat(target.match(searchTerm, 6)).isFalse()
        assertThat(target.match(searchTerm, 8)).isFalse()
    }

    @Test
    fun match_shouldReturnFalseIfOutOfBounds() {
        val target = MathAsmSentence(longArrayOf(1, 2, 1, 3, 1, 2, 2, 1, 2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1, 2), false)

        assertThat(target.match(searchTerm, 2362)).isFalse()
        assertThat(target.match(target, 1)).isFalse()
    }

    @Test
    fun match_shouldReturnFalseForLargerSearchTerms() {
        val target = MathAsmSentence(longArrayOf(1, 2), false)
        val searchTerm = MathAsmSentence(longArrayOf(1, 2, 1, 3, 1, 2, 2, 1, 2), false)

        assertThat(target.match(searchTerm, 0)).isFalse()
        assertThat(target.match(searchTerm, 1)).isFalse()
        assertThat(target.match(searchTerm, 242)).isFalse()
    }
}
