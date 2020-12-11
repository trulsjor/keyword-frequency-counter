package no.trulsjor.keywordfrequencycounter

import kotlinx.coroutines.runBlocking
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import java.io.File

fun main() = runBlocking {
    val config = Configuration()
    val directories = parseDirectories(keywordPath = config.paths.keywordsFile, inputPath = config.paths.inputDir)
    val outputPath = config.paths.outputDir
    writeGrandTotalToCSV(File(outputPath).resolve("grandtotal.csv"), directories)
    writeEachDirectoryToCSV(File(outputPath), directories)
}

internal fun parseDirectories(
    keywordPath: String,
    inputPath: String
): List<Directory> {
    val keywords = File(keywordPath).readLines()
    return File(inputPath).subDirsOf().map { dir ->
        val allKeywordFrequencyFilesInDir = dir.filesInDir().map { parseKeywordFrequencyFile(it, keywords) }
        Directory(dir.name, allKeywordFrequencyFilesInDir.toList())
    }.toList()
}

private fun writeEachDirectoryToCSV(
    outputPath: File,
    directories: List<Directory>
) {
    directories.sortedByDescending { it.grandTotal() }.forEach { it.writeCSVFile(outputPath) }
}

private fun writeGrandTotalToCSV(
    outputPath: File,
    directories: List<Directory>
) {
    outputPath.printWriter().use { writer ->
        directories
            .sortedByDescending { it.grandTotal() }
            .forEach { writer.println("${it.directoryName}, ${it.grandTotal()}") }
    }
}

private fun File.subDirsOf(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filter { it.isDirectory }.filter { this != it }

private fun File.filesInDir(): Sequence<File> =
   this.walkTopDown().maxDepth(1).filterNot { it.isDirectory }

private fun parseKeywordFrequencyFile(file: File, keywords: List<String>): KeywordFrequencyFile {
    val parser = AutoDetectParser()
    val metadata = Metadata()
    val handler = KeywordFrequencyContentHandler(BodyContentHandler(), metadata, keywords)
    parser.parse(file.inputStream(), handler, metadata, ParseContext())
    return KeywordFrequencyFile(file.name, keywords.map { it to metadata[it].toInt() }.toMap())
}
