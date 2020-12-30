package no.trulsjor.keywordfrequencycounter.matches

import no.trulsjor.keywordfrequencycounter.Keyword

internal class Match(
    internal val keyword: Keyword,
    internal val matchCount: Int,
    internal val matchesContext: List<Pair<String, String>>
)

internal fun List<Match>.countFor(key: String): Int = this.first { it.keyword.name == key }.matchCount
internal fun List<Match>.totalCount(): Int = this.map { it.matchCount }.sum()
