package no.trulsjor.keywordfrequencycounter

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ConfigurationTest {

    private val props = mapOf(
        "path.input.directory" to "src/test/resources/root/",
        "path.input.keywords" to "src/test/resources/keywords/radiohead.yml"
    )

    private fun configWithTestProps(): Configuration {

        for ((k, v) in props) {
            System.getProperties()[k] = v
        }

        val configuration = Configuration()

        for ((k, _) in props) {
            System.getProperties().remove(k)
        }
        return configuration
    }

    @Test
    internal fun `should pick up props`() {
        val configuration = configWithTestProps()
        Assertions.assertThat(configuration.paths.inputDir).isEqualTo(props["path.input.directory"])
        Assertions.assertThat(configuration.paths.keywordsFile).isEqualTo(props["path.input.keywords"])
    }
}