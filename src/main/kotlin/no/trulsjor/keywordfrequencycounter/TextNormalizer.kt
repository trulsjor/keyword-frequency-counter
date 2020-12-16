package no.trulsjor.keywordfrequencycounter

class TextNormalizer {

    private val doc = mutableListOf<String>()

    internal fun append(text: String) = doc.addAll(
        text.split(" ")
            .asSequence()
            .filter(String::isNotBlank)
            .map { it.replace("\n", " ") }
            .map { it.replace("-", " ") }
            .map { it.replace(",", " ") }
            .map { it.replace(".", " ") }
            .map { it.replace("!", " ") }
            .map { it.replace("?", " ") }
            .map { it.replace("(", " ") }
            .map { it.replace(")", " ") }
            .map { it.replace("/", " ") }
            .map { it.replace("’", " ") }
            .map { it.replace("”", " ") }
            .map { it.replace("“", " ") }
            .map { it.replace("'", " ") }
            .map { it.replace("\"", " ") }
            .map(String::toLowerCase)
            .map(String::trim)
            .toList())

    internal fun normalize() = " ${doc.joinToString(" ") { i -> i.replace("\\s+".toRegex(), " ") }} "
}