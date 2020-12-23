package no.trulsjor.keywordfrequencycounter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class KeywordTest {

    @Test
    internal fun `get all keywords`() {
        val keywords = Keywords.of(File("src/test/resources/keywords.yml").readText())
        assertThat(keywords.size()).isEqualTo(6)
    }


    @Test
    internal fun `get all keywords for category`() {
        val keywords = Keywords.of(File("src/test/resources/keywords.yml").readText())
        assertThat(keywords.forCategory("member")).hasSize(2)
        assertThat(keywords.forCategory("album")).hasSize(4)
    }

    @Test
    internal fun `gets all keyword alternatives`() {
        val keywords = "keywords:\n" +
                "  - name: 1\n" +
                "    alternatives:\n" +
                "      - 2\n" +
                "      - 3\n" +
                "      - 4\n"
        val keywordsWithAlternatives = Keywords.of(keywords).keywordWithAlternatives()["1"]
        assertThat(keywordsWithAlternatives).containsExactlyInAnyOrder("1", "2", "3", "4")
    }

}