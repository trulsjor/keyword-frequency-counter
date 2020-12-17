package no.trulsjor.keywordfrequencycounter.tikahandler

import org.apache.tika.metadata.Metadata
import org.apache.tika.sax.ContentHandlerDecorator
import org.xml.sax.ContentHandler
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class KeywordFrequencyContentHandler(
    handler: ContentHandler,
    private val metadata: Metadata,
    private val keywords: List<String>,
    private val contextLengthBefore: Int = 20,
    private val contextLengthAfter: Int = 20

) : ContentHandlerDecorator(handler) {

    private val textNormalizer = TextNormalizer()

    internal constructor(metadata: Metadata, vararg keywords: String) : this(
        DefaultHandler(),
        metadata,
        keywords.asList()
    ) {
    }

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
        keywords.forEach { keyword ->
            val allMatchesCount = Regex("\\b$keyword\\b").findAll(words).count()
            val context =
                Regex("\\b$keyword\\b").findAll(words).map { result -> getContextFor(result, words) }.toList()
            metadata.add(keyword, allMatchesCount.toString())
            context.forEach {
                metadata.add("$keyword-context", it)
            }
        }
    }

    private fun getContextFor(result: MatchResult, words: String): String {
        val start = (result.range.first - contextLengthBefore).coerceAtLeast(0)
        val end = (result.range.last + contextLengthAfter).coerceAtMost(words.length - 1)
        return words.substring(start, end).replace(result.value, result.value.toUpperCase())
    }
}
