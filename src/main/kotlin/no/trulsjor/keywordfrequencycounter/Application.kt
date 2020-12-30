package no.trulsjor.keywordfrequencycounter

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import no.trulsjor.keywordfrequencycounter.matches.Directory
import no.trulsjor.keywordfrequencycounter.matches.Match
import no.trulsjor.keywordfrequencycounter.matches.MatchFile
import no.trulsjor.keywordfrequencycounter.matches.writeFullCSV
import no.trulsjor.keywordfrequencycounter.matches.writeGrandTotalCSV
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
        val config = Configuration()
        val outputPath = File(config.paths.outputDir)
        val keywords = Keywords.fromYamlString(File(config.paths.keywordsFile).readText())
        measureTimeMillis {
            val directories = parseDirectories(inputPath = config.paths.inputDir, keywords = keywords)
            directories.writeFullCSV(outputPath.resolve("full.csv"))
            directories.writeGrandTotalCSV(outputPath.resolve("grandtotal.csv"))
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
                matchCount = metadata["${it.name}-count"].toInt(),
                matchesContext = metadata.getValues("${it.name}-context-first").toList()
                    .zip(metadata.getValues("${it.name}-context-second").toList()),

                )
        }
    )
}

private fun File.subDirsOf(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filter { it.isDirectory }.filter { this != it }

private fun File.filesInDir(): Sequence<File> =
    this.walkTopDown().maxDepth(1).filterNot { it.isDirectory }
