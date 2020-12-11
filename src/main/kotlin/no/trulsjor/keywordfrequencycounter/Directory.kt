package no.trulsjor.keywordfrequencycounter

import java.io.File

class KeywordFrequencyFile(
    private val fileName: String,
    private val matches: Map<String, Int>
) {
    internal fun matchesFor(keyword: String) = matches.getOrDefault(keyword, 0)
    internal fun total() = matches.map { it.value }.sum()
    internal fun rowAsCSV() = matches.entries.map { "$fileName,${it.key},${it.value}" }
}

class Directory(
    internal val directoryName: String,
    private val keywordFrequencyFiles: List<KeywordFrequencyFile>
) {
    internal fun totalFor(keyword: String): Int = keywordFrequencyFiles.map { it.matchesFor(keyword) }.sum()
    internal fun grandTotal(): Int = keywordFrequencyFiles.map { it.total() }.sum()

    internal fun writeCSVFile(path: File) {

        val matches = keywordFrequencyFiles.flatMap { it.rowAsCSV() }.toMutableList()
        matches.add(0, "File,Keyword,Count")
        path.resolve("k_$directoryName.csv").printWriter().use { writer ->
            matches.forEach { line ->
                writer.println(line)
            }
        }
    }
}
