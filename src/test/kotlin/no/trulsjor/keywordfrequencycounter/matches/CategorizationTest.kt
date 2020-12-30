package no.trulsjor.keywordfrequencycounter.matches

import no.trulsjor.keywordfrequencycounter.Keyword
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CategorizationTest {
    private val matchFile = MatchFile(
        "filename",
        listOf(
            Match(Keyword("1a", "1"), 1, emptyList()),
            Match(Keyword("1b", "1"), 1, emptyList()),
            Match(Keyword("1c", "1"), 1, emptyList()),
            Match(Keyword("2a", "2"), 1, emptyList()),
            Match(Keyword("2b", "2"), 1, emptyList())
        )
    )

    @Test
    internal fun `keyword with category`() {
        assertThat(matchFile.groupByCategory().keys).containsExactlyInAnyOrder("1", "2")
        assertThat(matchFile.groupByCategory()["1"]).hasSize(3)
        assertThat(matchFile.groupByCategory()["2"]).hasSize(2)
    }

    @Test
    fun `group By Categories are flatmapped`() {
        val directory = Directory("", listOf(matchFile, matchFile, matchFile))
        assertThat(directory.groupByCategories().keys).containsExactlyInAnyOrder("1", "2")
        assertThat(directory.groupByCategories()["1"]).hasSize(3 * 3)
        assertThat(directory.groupByCategories()["2"]).hasSize(2 * 3)
    }
}
