package no.trulsjor.keywordfrequencycounter.matches

import java.io.File

internal class Directory(
    internal val directoryName: String,
    private val matchFiles: List<MatchFile>
) {
    internal fun totalForKeywordWithName(keyword: String): Int =
        matchFiles.map { it.countFor(keyword) }.sum()

    internal fun grandTotal() = matchFiles.map { it.totalCount() }.sum()
    internal fun asCSV() = matchFiles.flatMap { it.contextRowAsCSV("$directoryName,") }

    internal fun groupByCategories(): Map<String, List<Match>> = matchFiles
        .map { it.groupByCategory() }
        .flatMap { it.entries }
        .groupBy { it.key }
        .mapValues { entry -> entry.value.map { it.value }.flatten() }
}

internal fun List<Directory>.writeGrandTotalCSV(path: File) {
    val header = listOf("Directory,Value")
    val lines = this.sortedByDescending { it.grandTotal() }.map { "${it.directoryName}, ${it.grandTotal()}" }
    writeLines(path, header + lines)
}

internal fun List<Directory>.writeFullCSV(path: File) {
    val header = listOf("Directory,File,Keyword,Category,Match On,Context")
    val lines = this.sortedByDescending { it.grandTotal() }
        .map { it.asCSV() }
        .flatMap { it.toList() }
    writeLines(path, header + lines)
}

private fun writeLines(path: File, lines: List<String>) {
    path.printWriter().use { writer ->
        lines.forEach { line ->
            writer.println(line)
        }
    }
}
