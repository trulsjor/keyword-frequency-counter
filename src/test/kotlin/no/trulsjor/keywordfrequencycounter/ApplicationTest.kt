package no.trulsjor.keywordfrequencycounter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ApplicationTest {

    class ConfigurationTest {
        private val defaultProps = mapOf(
            "path.input.directory" to "src/test/resources/root/",
            "path.input.keywords" to "src/test/resources/root/keywords.txt"
        )

        private fun withProps(test: () -> Unit) {
            val mergedProps = defaultProps
            for ((k, v) in mergedProps) {
                System.getProperties()[k] = v
            }
            test()
            for ((k, _) in mergedProps) {
                System.getProperties().remove(k)
            }
        }

        @Test
        internal fun `should parse directory for keywords`() {
            withProps() {
                val configuration = Configuration()
                assertThat(configuration.paths.inputDir).isEqualTo("src/test/resources/root/")
                assertThat(configuration.paths.keywordsFile).isEqualTo("src/test/resources/root/keywords.txt")

                val directories = parseDirectories(configuration.paths.keywordsFile, configuration.paths.inputDir)
                assertThat(directories).hasSize(1)
                val directory = directories.first()
                assertThat(directory.directoryName).isEqualTo("directory")
                assertThat(directory.totalFor("radiohead")).isEqualTo(68)
                assertThat(directory.totalFor("amnesiac")).isEqualTo(22)
                assertThat(directory.totalFor("pyramid song")).isEqualTo(51)
                assertThat(directory.totalFor("thom yorke")).isEqualTo(9)
                assertThat(directory.totalFor("volume 19 number 1 march 2013")).isEqualTo(1)
                assertThat(directory.totalFor("genius")).isEqualTo(2)
                assertThat(directory.totalFor("assistant")).isEqualTo(1)
                assertThat(directory.grandTotal()).isEqualTo(154)
            }
        }
    }
}
