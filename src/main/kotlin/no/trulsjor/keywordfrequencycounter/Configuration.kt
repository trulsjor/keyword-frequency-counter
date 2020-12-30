package no.trulsjor.keywordfrequencycounter

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

private fun config() = systemProperties() overriding EnvironmentVariables overriding ConfigurationProperties.fromResource("localpaths.properties") overriding ConfigurationProperties.fromResource(
    "paths.properties"
)

data class Configuration(
    val paths: Paths = Paths()
) {

    data class Paths(

        val inputDir: String = config()[Key("path.input.directory", stringType)],
        val keywordsFile: String = config()[Key("path.input.keywords", stringType)],
        val outputDir: String = config()[Key("path.output.directory", stringType)]
    )
}
