package no.trulsjor.keywordfrequencycounter

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import no.trulsjor.keywordfrequencycounter.matches.Directory
import no.trulsjor.keywordfrequencycounter.matches.Match
import no.trulsjor.keywordfrequencycounter.matches.MatchFile
import no.trulsjor.keywordfrequencycounter.matches.grandTotal
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
            val keywords = Keywords.fromYamlString(File(config.paths.keywordsFile).readText())
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
    val allMatchFilesInDir = coroutineScope {
        dir.filesInDir().map {
            async { parseMatchFile(it, keywords) }
        }.toList()
    }
    val matchFiles = allMatchFilesInDir.awaitAll()
    return Directory(dir.name, matchFiles)
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

private fun parseMatchFile(file: File, keywords: Keywords): MatchFile {
    val parser = AutoDetectParser()
    val metadata = Metadata()
    val handler = KeywordFrequencyContentHandler(
        handler = BodyContentHandler(),
        metadata = metadata,
        keywords = keywords
    )
    parser.parse(file.inputStream(), handler, metadata, ParseContext())
    return MatchFile(
        fileName = file.name,
        matches = keywords.keywords.map {
            Match(
                keyword = it,
                matchCount = metadata[it.name].toInt(),
                matchesContext = metadata.getValues("${it.name}-context").toList()
            )
        }
    )
}

private fun File.subDirsOf(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filter { it.isDirectory }.filter { this != it }

private fun File.filesInDir(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filterNot { it.isDirectory }
