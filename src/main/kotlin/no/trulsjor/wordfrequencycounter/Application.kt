package no.trulsjor.wordfrequencycounter

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
    writeGrandTotalToCSVFile(File(outputPath).resolve("grandtotal.csv"), directories)
    writeEachDirectoryToCSV(File(outputPath), directories)
}

internal fun parseDirectories(
    keywordPath: String,
    inputPath: String
): List<Directory> {
    val keywords = File(keywordPath).readLines()
    return inputPath.subDirsOf().map { dir ->
        val allMatchesInDirectory = dir.filesInDir().map { parseToWordFrequencyFile(it, keywords) }.toList()
        Directory(dir.name, allMatchesInDirectory)
    }.toList()
}

private fun writeEachDirectoryToCSV(
    outputPath: File,
    directories: List<Directory>
) {
    directories.sortedByDescending { it.grandTotal() }.forEach { it.writeFile(outputPath) }
}

private fun writeGrandTotalToCSVFile(
    outputPath: File,
    directories: List<Directory>
) {
    outputPath.printWriter().use { writer ->
        directories
            .sortedByDescending { it.grandTotal() }
            .forEach { writer.println("${it.directoryName}, ${it.grandTotal()}") }
    }
}

private fun String.subDirsOf(): Sequence<File> =
    File(this).walkTopDown().maxDepth(1).filter { it.isDirectory }.filter { it != File(this) }

private fun File.filesInDir(): Sequence<File> =
    File(this.absolutePath).walkTopDown().maxDepth(1).filterNot { it.isDirectory }

private fun parseToWordFrequencyFile(file: File, keywords: List<String>): WordFrequencyFile {
    val parser = AutoDetectParser()
    val metadata = Metadata()
    val handler = WordFrequencyContentHandler(BodyContentHandler(), metadata, keywords)
    parser.parse(file.inputStream(), handler, metadata, ParseContext())
    return WordFrequencyFile(file.name, keywords.map { it to metadata[it].toInt() }.toMap())
}



