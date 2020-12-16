package no.trulsjor.keywordfrequencycounter

import org.apache.tika.metadata.Metadata
import org.apache.tika.sax.ContentHandlerDecorator
import org.xml.sax.ContentHandler
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class KeywordFrequencyContentHandler(
    handler: ContentHandler,
    private val metadata: Metadata,
    private val keywords: List<String>
) :

    ContentHandlerDecorator(handler) {

    internal constructor(metadata: Metadata, vararg keywords: String) : this(
        DefaultHandler(),
        metadata,
        keywords.asList()
    ) {
    }


    internal val textNormalizer = TextNormalizer()

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
            val countRegex = Regex(" $keyword ").findAll(words).count()
            metadata.add(keyword, countRegex.toString())
        }
    }
}
