package no.trulsjor.keywordfrequencycounter

class Directory(
    internal val directoryName: String,
    private val keywordFrequencyFiles: List<KeywordFrequencyFile>
) {
    internal fun totalForKeywordWithName(keyword: String): Int =
        keywordFrequencyFiles.map { it.matchesFor(keyword) }.sum()

    internal fun grandTotal() = keywordFrequencyFiles.map { it.total() }.sum()
    internal fun asCSV() = keywordFrequencyFiles.flatMap { it.rowAsCSV() }
    internal fun asCSVWithContext() = keywordFrequencyFiles.flatMap { it.contextRowAsCSV() }
    internal fun getCategories(): Map<String, List<Match>> = keywordFrequencyFiles
        .map { it.groupByCategory() }
        .flatMap { it.entries }
        .groupBy { it.key }
        .mapValues { entry -> entry.value.map { it.value }.flatten() }

}

internal fun List<Directory>.grandTotal() =
    sortedByDescending { it.grandTotal() }.map { "${it.directoryName}, ${it.grandTotal()}" }


class KeywordFrequencyFile(
    private val fileName: String,
    internal val matches: List<Match>,
) {
    internal fun matchesFor(keyword: String): Int = matches.matchesFor(keyword)
    internal fun total() = matches.allMatchesCount()
    internal fun groupByCategory() = matches.groupBy { it.keyword.category }

    internal fun rowAsCSV() =
        matches.map { match ->
            "$fileName, ${match.keyword.name}, ${match.keyword.category}, ${match.matchCount}"
        }

    internal fun contextRowAsCSV() =
        matches.flatMap { match ->
            match.matchesContext.map {
                "$fileName, ${match.keyword.name}, ${match.keyword.category}, $it"
            }
        }
}

class Match(
    internal val keyword: Keyword,
    internal val matchCount: Int,
    internal val matchesContext: List<String>

)

internal fun List<Match>.matchesFor(key: String): Int {
    return this.first { it.keyword.name == key }.matchCount
}
internal fun List<Match>.allMatchesCount(): Int {
    this.map { it.matchCount }.sum()
}
