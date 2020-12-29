package no.trulsjor.keywordfrequencycounter

import kotlinx.coroutines.runBlocking
import no.trulsjor.keywordfrequencycounter.matches.countFor
import no.trulsjor.keywordfrequencycounter.matches.totalCount
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
                keywords = Keywords.fromYamlFileName(File("$keywordDir/radiohead.yml"))
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
                keywords = Keywords.fromYamlFileName(File("$keywordDir/alternatives.yml"))
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
                keywords = Keywords.fromYamlFileName(File("$keywordDir/categories.yml"))
            )
            val directory = directories.first()
            assertThat(directory.groupByCategories()).hasSize(2)
            assertThat(directory.groupByCategories().keys).containsExactlyInAnyOrder("album", "member")
            val members = directory.groupByCategories()["member"].orEmpty()
            val albums = directory.groupByCategories()["album"].orEmpty()

            assertThat(members).hasSize(5)
            assertThat(members.countFor("thom yorke")).isEqualTo(9)
            assertThat(members.countFor("jonny greenwood")).isEqualTo(2)
            assertThat(members.countFor("colin greenwood")).isEqualTo(1)
            assertThat(members.countFor("ed o brien")).isEqualTo(1)
            assertThat(members.countFor("phil selway")).isEqualTo(3)
            assertThat(members.totalCount()).isEqualTo(16)

            assertThat(albums).hasSize(9)
            assertThat(albums.countFor("pablo honey")).isEqualTo(0)
            assertThat(albums.countFor("the bends")).isEqualTo(4)
            assertThat(albums.countFor("ok computer")).isEqualTo(1)
            assertThat(albums.countFor("kid a")).isEqualTo(11)
            assertThat(albums.countFor("amnesiac")).isEqualTo(25)
            assertThat(albums.countFor("hail to the thief")).isEqualTo(2)
            assertThat(albums.countFor("in rainbows")).isEqualTo(1)
            assertThat(albums.countFor("the king of limbs")).isEqualTo(2)
            assertThat(albums.countFor("a moon shaped pool")).isEqualTo(0)
            assertThat(albums.totalCount()).isEqualTo(46)
        }
    }
}
