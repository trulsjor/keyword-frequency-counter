package no.trulsjor.wordfrequencycounter

import org.apache.tika.metadata.Metadata
import org.apache.tika.sax.ContentHandlerDecorator
import org.xml.sax.ContentHandler
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler


class WordFrequencyContentHandler(
    handler: ContentHandler,
    private val metadata: Metadata,
    private val keywords: List<String>
) :

    ContentHandlerDecorator(handler) {

    internal constructor(metadata: Metadata, vararg keywords:String) : this(DefaultHandler(), metadata, keywords.asList()) {}

    internal val doc = mutableListOf<String>()

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        try {
            val text = String(ch.copyOfRange(start, start + length))
            doc.addAll(text.split(" ")
                .asSequence()
                .filter(String::isNotBlank)
                .map { it.replace("\n"," ")  }
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
            super.characters(ch, start, length)
        } catch (e: SAXException) {
            handleException(e)
        }
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
        val words = " ${doc.joinToString(" ")} "
        keywords.forEach { keyword ->
            val countRegex = Regex(" $keyword ").findAll(words).count()
            metadata.add(keyword, countRegex.toString())

        }
    }


}