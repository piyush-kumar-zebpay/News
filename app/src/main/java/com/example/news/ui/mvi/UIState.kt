package com.example.news.ui.mvi

import com.example.news.data.remote.model.Article

data class NewsState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null
)