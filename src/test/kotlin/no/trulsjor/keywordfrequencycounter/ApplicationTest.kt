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
            assertThat(directory.getCategories().keys).containsExactlyInAnyOrder("album", "member")
            val members = directory.getCategories()["member"].orEmpty()
            val albums = directory.getCategories()["album"].orEmpty()

            assertThat(members).hasSize(5)
            assertThat(members.matchesFor("thom yorke")).isEqualTo(9)
            assertThat(members.matchesFor("jonny greenwood")).isEqualTo(2)
            assertThat(members.matchesFor("colin greenwood")).isEqualTo(1)
            assertThat(members.matchesFor("ed o brien")).isEqualTo(1)
            assertThat(members.matchesFor("phil selway")).isEqualTo(3)

            assertThat(albums).hasSize(9)
            assertThat(albums.matchesFor("pablo honey")).isEqualTo(0)
            assertThat(albums.matchesFor("the bends")).isEqualTo(4)
            assertThat(albums.matchesFor("ok computer")).isEqualTo(1)
            assertThat(albums.matchesFor("kid a")).isEqualTo(11)
            assertThat(albums.matchesFor("amnesiac")).isEqualTo(25)
            assertThat(albums.matchesFor("hail to the thief")).isEqualTo(2)
            assertThat(albums.matchesFor("in rainbows")).isEqualTo(1)
            assertThat(albums.matchesFor("the king of limbs")).isEqualTo(2)
            assertThat(albums.matchesFor("a moon shaped pool")).isEqualTo(0)
            assertThat(albums.allMatches()).isEqualTo(45)

        }
    }
}


