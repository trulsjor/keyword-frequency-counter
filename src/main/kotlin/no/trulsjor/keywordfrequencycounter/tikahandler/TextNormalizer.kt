package no.trulsjor.keywordfrequencycounter.tikahandler

class TextNormalizer {

    private val doc = mutableListOf<String>()
    private val specialCharacters = "[\\P{IsAlphabetic}&&\\P{Digit}&&[^\\s+]]".toRegex()

    internal fun append(text: String) = doc.add(text.toLowerCase().replace(specialCharacters, " "))

    internal fun normalize() =
        doc.joinToString(" ")
            .replace("\\s+".toRegex(), " ")
            .trim()
}
