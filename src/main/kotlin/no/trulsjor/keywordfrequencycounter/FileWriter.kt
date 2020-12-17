package no.trulsjor.keywordfrequencycounter

import java.io.File


internal fun writeCSVFile(path: File, lines: List<String>) {
    val matches = lines.toMutableList();
    matches.add(0, "File,Keyword,Value")
    path.printWriter().use { writer ->
        matches.forEach { line ->
            writer.println(line)
        }
    }
}