package no.trulsjor.keywordfrequencycounter.tikahandler

import org.apache.tika.metadata.Metadata
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class KeywordFrequencyContentHandlerTest {

    @Test
    internal fun `should put matches in metadata`() {
        val metadata = Metadata()
        val contentHandler = KeywordFrequencyContentHandler(metadata, "you", "follow", "the strangest tribe")
        val str = ("It's five below in evidence\n" +
                "the winded eves and sideways snow\n" +
                "his eminence has yet to show\n" +
                "FOLLOW the ageless tide\n" +
                "follow the angled light\n" +
                "follow the strangest tribe\n" +
                "it's 6:00 AM\n" +
                "you're waiting for\n" +
                "you've had your feast...you're wanting more\n" +
                "follow the wayward mile\n" +
                "follow the distant high\n" +
                "follow the strangest tribe\n" +
                "follow the ancient stripe\n" +
                "follow the angels try\n" +
                "follow the strangest tribe").toCharArray()
        contentHandler.characters(str, 0, str.size)
        contentHandler.endDocument()
        Assertions.assertThat(metadata["follow"]).isEqualTo("9")
        Assertions.assertThat(metadata["the strangest tribe"]).isEqualTo("3")
        Assertions.assertThat(metadata["you"]).isEqualTo("3")
    }
}
