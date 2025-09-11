package com.example.news.presentation.model

import com.example.news.domain.model.Article

data class NewsUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val error: String? = null
)

sealed class NewsIntent {
    data object LoadTopHeadlines : NewsIntent()
    data class SelectArticle(val index: Int) : NewsIntent()
}

sealed class NewsEffect {
    data class NavigateToDetail(val index: Int) : NewsEffect()
    data class ShowError(val message: String) : NewsEffect()
}
