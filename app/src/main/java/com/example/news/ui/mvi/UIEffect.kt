package com.example.news.ui.mvi

sealed class NewsEffect {
    data class ShowError(val message: String) : NewsEffect()
    data class NavigateToDetail(val index: Int) : NewsEffect()
}