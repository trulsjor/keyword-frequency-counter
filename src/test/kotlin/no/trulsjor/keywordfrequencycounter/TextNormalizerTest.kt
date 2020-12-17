package no.trulsjor.keywordfrequencycounter

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class TextNormalizerTest {

    @Test
    internal fun `should trim unwanted characters from string`() {
        val normalizer = TextNormalizer()
        normalizer.append("this-is.")
        normalizer.append(" the /String(yEs ")
        normalizer.append("I'm SERIOUS!!!")
        Assertions.assertThat(normalizer.normalize()).isEqualTo("this is the string yes i m serious")
    }
    @Test
    internal fun `international characters are ok`() {
        val normalizer = TextNormalizer()
        normalizer.append("æøåæøåâäôö is ok")
        Assertions.assertThat(normalizer.normalize()).isEqualTo("æøåæøåâäôö is ok")
    }

    @Test
    internal fun `numbers are ok`() {
        val normalizer = TextNormalizer()
        normalizer.append("A text containing digits 123,45:524, and 1 000 000 is ok....!   ")
        Assertions.assertThat(normalizer.normalize()).isEqualTo("a text containing digits 123 45 524 and 1 000 000 is ok")
    }


}

