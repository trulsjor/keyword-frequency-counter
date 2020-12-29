package no.trulsjor.keywordfrequencycounter

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import java.io.File


@Serializable
data class Keyword(
    val name: String,
    val category: String = "Standard",
    val alternatives: List<String> = emptyList()
) {
    fun all() = alternatives + name
}


@Serializable
data class Keywords( val keywords: List<Keyword>) {
    fun size() = keywords.size

    fun keywordWithAlternatives() = keywords.map { it.name to it.all() }.toMap()
    fun forCategory(category: String) = keywords.filter { category == it.category }
    fun byCategory() = keywords.map { it.category to it }.toMap()

    companion object {
        fun fromYamlString(yaml: String): Keywords = Yaml.default.decodeFromString(serializer(), yaml)
        fun fromYamlFileName(filename: File) = Yaml.default.decodeFromString(serializer(), filename.readText())
        fun fromNamesAsStrings(vararg keywords: String) = Keywords(keywords.map { Keyword(it) })
    }
}



