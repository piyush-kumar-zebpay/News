package com.example.news.domain.repository

import com.example.news.data.model.Bookmarks
import com.example.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {
    val bookmarksFlow: Flow<List<Bookmarks.Article>>

    suspend fun addBookmark(article: Article)

    suspend fun removeBookmark(articleUrl: String)
}
