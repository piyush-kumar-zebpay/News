package com.example.news.domain.model

data class BookmarkedArticle (
    val sourceName: String,
    val sourceId: String?,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val content: String?
)