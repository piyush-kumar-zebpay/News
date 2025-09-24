package com.example.news.presentation.model

import com.example.news.domain.model.Article

data class NewsUiState(
    val newsArticles: List<Article> = emptyList(),
    val videoArticles: List<Article> = emptyList(),
    val bookmarkedArticles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val error: String? = null
)

//sealed class NewsIntent {
//    object LoadBookmarks : NewsIntent()
//    data class SelectArticle(val articleId: String) : NewsIntent()
//    data class ToggleBookmark(val article: Article) : NewsIntent()
//}

sealed class NewsEffect {
    data class NavigateToDetail(val index: Int) : NewsEffect()
    data class Error(val message: String) : NewsEffect()
}
