package no.trulsjor.keywordfrequencycounter

import java.io.File

internal fun writeCSVFile(path: File, lines: List<String>) {
    val matches = lines.toMutableList()
    matches.add(0, "File,Keyword,Category,Match On,Value")
    writeLines(path, matches)
}

internal fun writeGrandTotalCSVFile(path: File, lines: List<String>) {
    val matches = lines.toMutableList()
    matches.add(0, "Directory,Value")
    writeLines(path, matches)
}


internal fun writeLines(path: File, lines: List<String>) {
    path.printWriter().use { writer ->
        lines.forEach { line ->
            writer.println(line)
        }
    }
}
