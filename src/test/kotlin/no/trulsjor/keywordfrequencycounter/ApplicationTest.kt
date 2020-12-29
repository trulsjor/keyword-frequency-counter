package no.trulsjor.keywordfrequencycounter

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class ApplicationTest {

    private val contentDir = "src/test/resources/root/"
    private val keywordDir = "src/test/resources/keywords"


    @Test
    internal fun `should parse directory for keywords`() {
        runBlocking {
            val directories = parseDirectories(
                contentDir,
                keywords = Keywords.fromYamlFileName(File("${keywordDir}/radiohead.yml"))
            )

            assertThat(directories).hasSize(1)
            val directory = directories.first()
            assertThat(directory.directoryName).isEqualTo("directory")
            assertThat(directory.totalForKeywordWithName("radiohead")).isEqualTo(83)
            assertThat(directory.totalForKeywordWithName("amnesiac")).isEqualTo(25)
            assertThat(directory.totalForKeywordWithName("pyramid song")).isEqualTo(61)
            assertThat(directory.totalForKeywordWithName("thom yorke")).isEqualTo(9)
            assertThat(directory.totalForKeywordWithName("volume 19 number 1 march 2013")).isEqualTo(1)
            assertThat(directory.totalForKeywordWithName("genius")).isEqualTo(2)
            assertThat(directory.totalForKeywordWithName("assistant")).isEqualTo(1)
            assertThat(directory.grandTotal()).isEqualTo(182)
        }
    }


    @Test
    internal fun `should treat alternatives equal to keyword name`() {
        runBlocking {
            val directories = parseDirectories(
                inputPath = contentDir,
                keywords = Keywords.fromYamlFileName(File("${keywordDir}/alternatives.yml"))
            )
            val directory = directories.first()
            assertThat(directory.totalForKeywordWithName("pyramid song")).isEqualTo(147)
            assertThat(directory.grandTotal()).isEqualTo(147)

        }
    }


    @Test
    internal fun `should group by category `() {
        runBlocking {
            val directories = parseDirectories(
                inputPath = contentDir,
                keywords = Keywords.fromYamlFileName(File("${keywordDir}/categories.yml"))
            )
            val directory = directories.first()
            assertThat(directory.getCategories()).hasSize(2)
            val members = directory.getCategories()["member"]
            val albums = directory.getCategories()["album"]

            assertThat(members).hasSize(5)
            assertThat(members)
            assertThat(albums).hasSize(9)


        }
    }
}
