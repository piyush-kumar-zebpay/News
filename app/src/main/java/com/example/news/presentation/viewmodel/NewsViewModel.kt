package com.example.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.model.Article
import com.example.news.domain.repository.BookmarksRepository
import com.example.news.domain.usecase.GetInternetStatusUseCase
import com.example.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.news.presentation.model.NewsEffect
import com.example.news.presentation.model.NewsUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val internetStatusUseCase: GetInternetStatusUseCase,
    private val bookmarkRepository: BookmarksRepository
) : ViewModel() {
    private val _state = MutableSharedFlow<NewsUiState>(replay = 1)
    val state: SharedFlow<NewsUiState> = _state.asSharedFlow()
    private val _effect = MutableSharedFlow<NewsEffect>()
    val effect: SharedFlow<NewsEffect> = _effect.asSharedFlow()

    private var currentState = NewsUiState()

    init {
        viewModelScope.launch { _state.emit(currentState) }
        loadTopHeadlines()
        networkStatus()
        observeBookmarks()
    }
    private fun loadTopHeadlines() {
        viewModelScope.launch {
            currentState = currentState.copy(isLoading = true)
            _state.emit(currentState)
            try {
                when (val result = getTopHeadlinesUseCase("us")) {
                    is com.example.news.domain.model.Result.Success -> {
                        currentState = currentState.copy(
                            articles = result.data,
                            isLoading = false,
                            error = null
                        )
                        _state.emit(currentState)
                    }

                    is com.example.news.domain.model.Result.Error -> {
                        currentState = currentState.copy(
                            isLoading = false,
                            error = result.message
                        )
                        _state.emit(currentState)
                    }

                    is com.example.news.domain.model.Result.Loading -> {
                        currentState = currentState.copy(isLoading = true)
                        _state.emit(currentState)
                    }
                }
            } catch (e: Exception) {
                currentState = currentState.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
                _state.emit(currentState)
            }
        }
    }

    // --- NETWORK STATUS ---
    private fun networkStatus() {
        viewModelScope.launch {
            internetStatusUseCase().collect { isOnline ->
                currentState = currentState.copy(isOnline = isOnline)
                _state.emit(currentState)
            }
        }
    }

    // --- BOOKMARKS ---
    private fun observeBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.bookmarksFlow.collect { bookmarkedArticles ->
                val currentArticles = currentState.articles
                val filteredBookmarks = bookmarkedArticles.filter { bookmarked ->
                    currentArticles.any { it.url == bookmarked.url }
                }.map { article ->
                    com.example.news.domain.model.BookmarkedArticle(
                        url = article.url,
                        title = article.title,
                        author = article.author,
                        description = article.description,
                        imageUrl = article.imageUrl,
                        publishedAt = article.publishedAt,
                        content = article.content,
                        sourceName = article.source.name,
                        sourceId = article.source.id
                    )
                }
                currentState = currentState.copy(bookmarkedArticles = filteredBookmarks)
                _state.emit(currentState)
            }
        }
    }

    fun addBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkRepository.addBookmark(article)
        }
    }

    fun removeBookmark(articleUrl: String) {
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(articleUrl)
        }
    }

    fun loadNews(){
        loadTopHeadlines()
    }

}
