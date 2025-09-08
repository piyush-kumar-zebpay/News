package com.example.news.ui.mvi

sealed class NewsIntent {
    data class LoadNews(val country: String) : NewsIntent()
    object Refresh : NewsIntent()
    data class SelectArticle(val index: Int) : NewsIntent()
}