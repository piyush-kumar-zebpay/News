package com.example.news.data.model

import com.example.news.domain.model.Article
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArticleDto(
    @Json(name = "source")
    val source: SourceDto,
    @Json(name = "author")
    val author: String?,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "url")
    val url: String,
    @Json(name = "urlToImage")
    val imageUrl: String?,
    @Json(name = "publishedAt")
    val publishedAt: String,
    @Json(name = "content")
    val content: String?,
    val isBookmarked: Boolean = false
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
