package no.trulsjor.wordfrequencycounter

import java.io.File

class WordFrequencyFile(
    private val fileName: String,
    private val matches: Map<String, Int>
) {
    internal fun matchesFor(keyword: String) = matches.getOrDefault(keyword, 0)
    internal fun total() = matches.map { it.value }.sum()
    internal fun rowAsCSV() = matches.entries.map { "$fileName,${it.key},${it.value}" }



}

class Directory(
    internal val directoryName: String,
    private val wordFrequencyFiles: List<WordFrequencyFile>
) {
    internal fun totalFor(keyword: String): Int = wordFrequencyFiles.map { it.matchesFor(keyword) }.sum()
    internal fun grandTotal(): Int = wordFrequencyFiles.map { it.total() }.sum()

    internal fun writeFile(path: File) {

        val matches = wordFrequencyFiles.flatMap { it.rowAsCSV() }.toMutableList()
        matches.add(0, "File,Keyword,Count")
        path.resolve("k_$directoryName.csv").printWriter().use { writer ->
            matches.forEach { line ->
                writer.println(line)
            }
        }


    }
}


