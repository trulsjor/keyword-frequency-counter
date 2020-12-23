package no.trulsjor.keywordfrequencycounter

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable


@Serializable
data class Keyword(
    val name: String,
    val category: String = "Standard",
    val alternatives: List<String> = emptyList()
) {
    fun all() = alternatives + name
}


@Serializable
data class Keywords(private val keywords: List<Keyword>) {
    fun size() = keywords.size

    fun keywordWithAlternatives() = keywords.map { it.name to it.all() }.toMap()
    fun forCategory(category: String) = keywords.filter { category == it.category }

    companion object {
        fun of(file: String): Keywords = Yaml.default.decodeFromString(serializer(), file)
    }
}



