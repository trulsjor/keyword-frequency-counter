package no.trulsjor.wordfrequencycounter

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties


private fun config() =
    systemProperties() overriding EnvironmentVariables overriding ConfigurationProperties.fromResource(
        "paths.properties"
    )

data class Configuration(
    val paths: Paths = Paths()
) {

    data class Paths(

        val inputDir: String = config().get(Key("path.input.directory", stringType)),
        val keywordsFile: String = config()[Key("path.input.keywords", stringType)],
        val outputDir: String = config()[Key("path.output.directory", stringType)]
    )
}
