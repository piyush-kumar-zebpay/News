package com.example.news.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.data.remote.model.Article
import com.example.news.data.repository.NewsRepository
import com.example.news.ui.mvi.NewsEffect
import com.example.news.ui.mvi.NewsIntent
import com.example.news.ui.mvi.NewsState
import com.example.news.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val newsApi = NewsRepository()
    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state.asStateFlow()
    private val _effect = Channel<NewsEffect>()
    val effect = _effect.receiveAsFlow()
    fun handleIntent(intent: NewsIntent) {
        when (intent) {
            is NewsIntent.LoadNews -> fetchNews(intent.country)
            is NewsIntent.Refresh -> refreshNews()
            is NewsIntent.SelectArticle -> {
                viewModelScope.launch {
                    _effect.send(NewsEffect.NavigateToDetail(intent.index))
                }
            }
        }
    }
    private fun fetchNews(country: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = newsApi.getTopHeadlines(country, Constants.Companion.API_KEY)
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    val articles: List<Article> = newsResponse?.articles ?: emptyList()
                    _state.update { it.copy(isLoading = false, articles = articles) }
                } else {
                    val msg = "Network error: ${response.code()}"
                    _state.update { it.copy(isLoading = false, errorMessage = msg) }
                    _effect.send(NewsEffect.ShowError(msg))
                }
            } catch (e: Exception) {
                val msg = "Network error: ${e.localizedMessage}"
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
                _effect.send(NewsEffect.ShowError(msg))
            }
        }
    }
    private fun refreshNews() {
        fetchNews("us")
    }
}