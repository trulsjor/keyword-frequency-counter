package no.trulsjor.keywordfrequencycounter.tikahandler

import no.trulsjor.keywordfrequencycounter.Keywords
import org.apache.tika.metadata.Metadata
import org.apache.tika.sax.ContentHandlerDecorator
import org.xml.sax.ContentHandler
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class KeywordFrequencyContentHandler(
    handler: ContentHandler,
    private val metadata: Metadata,
    private val keywords: Keywords,
    private val contextLengthBefore: Int = 30,
    private val contextLengthAfter: Int = 30

) : ContentHandlerDecorator(handler) {

    private val textNormalizer = TextNormalizer()

    internal constructor(metadata: Metadata, vararg keywords: String) : this(
        handler = DefaultHandler(),
        metadata = metadata,
        keywords = Keywords.fromNamesAsStrings(*keywords)
    )

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        try {
            val text = String(ch.copyOfRange(start, start + length))
            textNormalizer.append(text)
            super.characters(ch, start, length)
        } catch (e: SAXException) {
            handleException(e)
        }
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
        val words = textNormalizer.normalize()

        keywords.keywordWithAlternatives().forEach { entry ->
            entry.value
                .map { toMatchesWithContext(it, words) }
                .flatMap { it.toList() }
                .forEach {
                    metadata.add("${entry.key}-context-first", it.first.trim())
                    metadata.add("${entry.key}-context-second", it.second.trim())
                }

            val allMatchesCount = entry.value
                .map { Regex("\\b$it\\b").findAll(words).count() }.sum()
            metadata.add("${entry.key}-count", allMatchesCount.toString())
        }
    }

    private fun toMatchesWithContext(candidate: String, words: String): List<Pair<String, String>> {
        return Regex("\\b$candidate\\b")
            .findAll(words)
            .map { result -> getContextFor(result, words) }
            .toList()
    }

    private fun getContextFor(result: MatchResult, words: String): Pair<String, String> {
        val start = (result.range.first - contextLengthBefore).coerceAtLeast(0)
        val end = (result.range.last + contextLengthAfter).coerceAtMost(words.length - 1)
        val context = words.substring(start, end).replace(result.value, result.value.toUpperCase())
        return result.value to context
    }
}
