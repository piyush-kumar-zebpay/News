package com.example.news.data.model

import com.example.news.domain.model.Article
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ArticleDto(
    @SerialName("source")
    val source: SourceDto,

    @SerialName("author")
    val author: String? = null,

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("url")
    val url: String,

    @SerialName("urlToImage")
    val imageUrl: String? = null,

    @SerialName("publishedAt")
    val publishedAt: String,

    @SerialName("content")
    val content: String? = null
) {
    fun toDomain(): Article = Article(
        sourceName = source.name,
        sourceId = source.id,
        author = author,
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        content = content
    )
}
