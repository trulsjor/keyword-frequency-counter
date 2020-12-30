package no.trulsjor.keywordfrequencycounter.matches

internal class MatchFile(
    private val fileName: String,
    internal val matches: List<Match>
) {
    internal fun countFor(keyword: String): Int = matches.countFor(keyword)
    internal fun totalCount() = matches.totalCount()
    internal fun groupByCategory(): Map<String, List<Match>> = matches.groupBy { it.keyword.category }

    internal fun rowAsCSV() =
        matches.map { match ->
            "$fileName, ${match.keyword.name}, ${match.keyword.category}, ,${match.matchCount}"
        }

    internal fun contextRowAsCSV() =
        matches.flatMap { match ->
            match.matchesContext.map {
                "$fileName, ${match.keyword.name}, ${match.keyword.category},  ${it.first}, ${it.second}"
            }
        }
}
