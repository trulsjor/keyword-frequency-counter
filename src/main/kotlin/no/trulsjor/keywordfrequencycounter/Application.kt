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
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
suspend fun main(): Unit = coroutineScope {
    launch {
        measureTimeMillis {
            val config = Configuration()
            val keywords = Keywords.fromFileName(File(config.paths.keywordsFile).readText())
            val directories = parseDirectories(inputPath = config.paths.inputDir, keywords = keywords)
            writeCSVFiles(outputPath = File(config.paths.outputDir), directories = directories)
        }.also {
            println("Finished in ${it.toDuration(DurationUnit.MILLISECONDS).inSeconds} seconds")
        }
    }
}

internal suspend fun parseDirectories(
    inputPath: String,
    keywords: Keywords
): List<Directory> {
    val directories = coroutineScope {
        File(inputPath).subDirsOf().map { dir ->
            async { parseDirectory(dir, keywords) }
        }.toList()
    }
    return directories.awaitAll()
}

private suspend fun parseDirectory(
    dir: File,
    keywords: Keywords
): Directory {
    val allKeywordFrequencyFilesInDir = coroutineScope {
        dir.filesInDir().map {
            async { parseKeywordFrequencyFile(it, keywords) }
        }.toList()
    }
    val keywordFrequencyFiles = allKeywordFrequencyFilesInDir.awaitAll()
    return Directory(dir.name, keywordFrequencyFiles)
}

private suspend fun writeCSVFiles(
    outputPath: File,
    directories: List<Directory>
) {
    directories.sortedByDescending { it.grandTotal() }.forEach {
        coroutineScope {
            launch {
                writeCSVFile(outputPath.resolve("${it.directoryName}.csv"), it.asCSV())
                writeCSVFile(outputPath.resolve("${it.directoryName}-context.csv"), it.asCSVWithContext())
            }
        }
    }
    writeCSVFile(outputPath.resolve("grandtotal.csv"), directories.grandTotal())
}

private fun parseKeywordFrequencyFile(file: File, keywords: Keywords): KeywordFrequencyFile {
    val parser = AutoDetectParser()
    val metadata = Metadata()
    val handler = KeywordFrequencyContentHandler(
        handler = BodyContentHandler(),
        metadata = metadata,
        keywords = keywords
    )
    parser.parse(file.inputStream(), handler, metadata, ParseContext())
    return KeywordFrequencyFile(
        fileName = file.name,
        matches = keywords.names().map { it to metadata[it].toInt() }.toMap(),
        matchesContext = keywords.names().map { it to metadata.getValues("$it-context").toList() }.toMap()
    )
}

private fun File.subDirsOf(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filter { it.isDirectory }.filter { this != it }

private fun File.filesInDir(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filterNot { it.isDirectory }
