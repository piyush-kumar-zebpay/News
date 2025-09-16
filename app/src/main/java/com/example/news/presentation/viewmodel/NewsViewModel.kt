package com.example.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.usecase.GetInternetStatusUseCase
import com.example.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.news.presentation.model.NewsEffect
import com.example.news.presentation.model.NewsUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.news.domain.repository.BookmarksRepository

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
        observeBookmarks() // ✅ start collecting bookmarks
    }

    // --- TOP HEADLINES ---
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
            bookmarkRepository.bookmarksFlow.collect { bookmarkedUrls ->
                currentState = currentState.copy(
                    bookmarkedArticles = bookmarkedUrls.map { url ->
                        com.example.news.domain.model.BookmarkedArticle(url)
                    }
                )
                _state.emit(currentState)
            }
        }
    }

    fun addBookmark(url: String) {
        viewModelScope.launch {
            bookmarkRepository.addBookmark(url)
        }
    }

    fun removeBookmark(url: String) {
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(url)
        }
    }
}
