package com.example.news.domain.model

data class HeadlinesResult(
    val newsArticles: List<Article>,
    val videoArticles: List<Article>
)
