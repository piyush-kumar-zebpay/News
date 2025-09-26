package com.example.news.domain.model

import com.example.news.data.model.Bookmarks
import kotlinx.coroutines.flow.Flow

data class HeadlinesResult(
    val newsArticles: List<Article>,
    val videoArticles: List<Article>,
    val isBookmarked : Boolean,
    val observeBookmarks: Flow<List<Article>>
)
