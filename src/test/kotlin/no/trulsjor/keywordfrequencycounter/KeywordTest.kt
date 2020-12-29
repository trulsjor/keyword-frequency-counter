package no.trulsjor.keywordfrequencycounter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class KeywordTest {

    @Test
    internal fun `get all keywords`() {
        val keywords = Keywords.fromYamlString(File("src/test/resources/keywords/radiohead.yml").readText())
        assertThat(keywords.size()).isEqualTo(8)
    }


    @Test
    internal fun `get all keywords for category`() {
        val keywords = Keywords.fromYamlString(File("src/test/resources/keywords/categories.yml").readText())
        assertThat(keywords.forCategory("member")).hasSize(5)
        assertThat(keywords.forCategory("album")).hasSize(9)
    }

    @Test
    internal fun `gets all keyword alternatives`() {
        val keywords = "keywords:\n" +
                "  - name: 1\n" +
                "    alternatives:\n" +
                "      - 2\n" +
                "      - 3\n" +
                "      - 4\n"
        val keywordsWithAlternatives = Keywords.fromYamlString(keywords).keywordWithAlternatives()["1"]
        assertThat(keywordsWithAlternatives).containsExactlyInAnyOrder("1", "2", "3", "4")
    }

}