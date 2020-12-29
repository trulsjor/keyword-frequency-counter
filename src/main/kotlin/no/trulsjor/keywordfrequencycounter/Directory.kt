package no.trulsjor.keywordfrequencycounter

class Directory(
    internal val directoryName: String,
    private val keywordFrequencyFiles: List<KeywordFrequencyFile>
) {
    internal fun totalForKeywordWithName(keyword: String): Int = keywordFrequencyFiles.map { it.matchesFor(keyword) }.sum()

    internal fun grandTotal() = keywordFrequencyFiles.map { it.total() }.sum()
    internal fun asCSV() = keywordFrequencyFiles.flatMap { it.rowAsCSV() }
    internal fun asCSVWithContext() = keywordFrequencyFiles.flatMap { it.contextRowAsCSV() }
    internal fun getCategories(): Map<String, List<Keyword>> = keywordFrequencyFiles
        .map { it.groupByCategory() }
        .flatMap { it.entries }
        .groupBy { it.key }
        .mapValues { entry -> entry.value.map { it.value }.flatten() }

}

internal fun List<Directory>.grandTotal() =
    sortedByDescending { it.grandTotal() }.map { "${it.directoryName}, ${it.grandTotal()}" }

class KeywordFrequencyFile(
    private val fileName: String,
    internal val matches: Map<Keyword, Int>,
    private val matchesContext: Map<Keyword, List<String>>
) {
    internal fun matchesFor(keyword: String): Int = matches
        .mapKeys { it.key.name }
        .getOrDefault(keyword, 0)

    internal fun total() = matches.map { it.value }.sum()
    internal fun groupByCategory() = matches.keys.groupBy { it.category }

    internal fun rowAsCSV() =
        matches.entries.map { entry ->
            "$fileName, ${entry.key.name}, ${entry.key.category}, ${entry.value}"
        }

    internal fun contextRowAsCSV() =
        matchesContext.entries.flatMap { entry ->
            entry.value.map {
                "$fileName, ${entry.key.name}, ${entry.key.category}, $it"
            }
        }
}
