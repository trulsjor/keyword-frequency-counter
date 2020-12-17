package no.trulsjor.keywordfrequencycounter

class KeywordFrequencyFile(
    private val fileName: String,
    private val matches: Map<String, Int>,
    private val matchesContext: Map<String, List<String>>
) {
    internal fun matchesFor(keyword: String) = matches.getOrDefault(keyword, 0)
    internal fun total() = matches.map { it.value }.sum()
    internal fun rowAsCSV() = matches.entries.map { "$fileName, ${it.key}, ${it.value}" }
    internal fun contextRowAsCSV() =
        matchesContext.entries.flatMap { entry -> entry.value.map { "$fileName, ${entry.key}, ${it}" } }
}


class Directory(
    internal val directoryName: String,
    private val keywordFrequencyFiles: List<KeywordFrequencyFile>
) {
    internal fun totalFor(keyword: String): Int = keywordFrequencyFiles.map { it.matchesFor(keyword) }.sum()

    internal fun grandTotal() = keywordFrequencyFiles.map { it.total() }.sum()
    internal fun asCSV() = keywordFrequencyFiles.flatMap { it.rowAsCSV() }
    internal fun asCSVWithContext() = keywordFrequencyFiles.flatMap { it.contextRowAsCSV() }

}
    internal fun  List<Directory>.grandTotal() = sortedByDescending { it.grandTotal() }.map { "${it.directoryName}, ${it.grandTotal()}" }


