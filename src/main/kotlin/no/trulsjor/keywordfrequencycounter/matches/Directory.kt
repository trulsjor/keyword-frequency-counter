package no.trulsjor.keywordfrequencycounter.matches

internal class Directory(
    internal val directoryName: String,
    private val matchFiles: List<MatchFile>
) {
    internal fun totalForKeywordWithName(keyword: String): Int =
        matchFiles.map { it.countFor(keyword) }.sum()

    internal fun grandTotal() = matchFiles.map { it.totalCount() }.sum()

    internal fun asCSV() = matchFiles.flatMap { it.rowAsCSV() }
    internal fun asCSVWithContext() = matchFiles.flatMap { it.contextRowAsCSV() }

    internal fun groupByCategories(): Map<String, List<Match>> = matchFiles
        .map { it.groupByCategory() }
        .flatMap { it.entries }
        .groupBy { it.key }
        .mapValues { entry -> entry.value.map { it.value }.flatten() }
}

internal fun List<Directory>.grandTotal() =
    sortedByDescending { it.grandTotal() }.map { "${it.directoryName}, ${it.grandTotal()}" }
