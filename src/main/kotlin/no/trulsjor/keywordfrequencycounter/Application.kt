package no.trulsjor.keywordfrequencycounter

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import no.trulsjor.keywordfrequencycounter.tikahandler.KeywordFrequencyContentHandler
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import java.io.File

suspend fun main(): Unit = coroutineScope {
    launch {
        val config = Configuration()
        val directories = parseDirectories(keywordPath = config.paths.keywordsFile, inputPath = config.paths.inputDir)
        val outputPath = config.paths.outputDir
        writeCSVFiles(File(outputPath), directories)
    }
}

internal suspend fun parseDirectories(
    keywordPath: String,
    inputPath: String
): List<Directory> {
    val directories = coroutineScope {
        val keywords = File(keywordPath).readLines()
        File(inputPath).subDirsOf().map { dir ->
            async { parseDirectory(dir, keywords) }
        }.toList()

    }
    return directories.awaitAll()
}

private suspend fun parseDirectory(
    dir: File,
    keywords: List<String>
): Directory {
    val allKeywordFrequencyFilesInDir = coroutineScope {
        dir.filesInDir().map {
            async { parseKeywordFrequencyFile(it, keywords) }
        }.toList()
    }
    return Directory(dir.name, allKeywordFrequencyFilesInDir.awaitAll())
}


private fun writeCSVFiles(
    outputPath: File,
    directories: List<Directory>
) {
    directories.sortedByDescending { it.grandTotal() }.forEach {
        writeCSVFile(outputPath.resolve("${it.directoryName}.csv"), it.asCSV())
        writeCSVFile(outputPath.resolve("${it.directoryName}-context.csv"), it.asCSVWithContext())
    }
    writeCSVFile(outputPath.resolve("grandtotal.csv"), directories.grandTotal())
}

private fun parseKeywordFrequencyFile(file: File, keywords: List<String>): KeywordFrequencyFile {
    val parser = AutoDetectParser()
    val metadata = Metadata()
    val handler = KeywordFrequencyContentHandler(
        handler = BodyContentHandler(),
        metadata = metadata,
        keywords = keywords
    )
    parser.parse(file.inputStream(), handler, metadata, ParseContext())
    println("Parsed ${file.parentFile.name} -  ${file.name}")
    return KeywordFrequencyFile(
        fileName = file.name,
        matches = keywords.map { it to metadata[it].toInt() }.toMap(),
        matchesContext = keywords.map { it to metadata.getValues("$it-context").toList() }.toMap()
    )
}

private fun File.subDirsOf(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filter { it.isDirectory }.filter { this != it }

private fun File.filesInDir(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filterNot { it.isDirectory }
