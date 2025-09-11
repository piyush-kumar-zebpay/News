package com.example.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.usecase.GetInternetStatusUseCase
import com.example.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.news.presentation.model.NewsEffect
import com.example.news.presentation.model.NewsIntent
import com.example.news.presentation.model.NewsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class NewsViewModel(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val internetStatusUseCase: GetInternetStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsUiState())
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewsEffect>()
    val effect: SharedFlow<NewsEffect> = _effect.asSharedFlow()

    init {
        handleIntent(NewsIntent.LoadTopHeadlines)
        networkStatus()
    }

    fun handleIntent(intent: NewsIntent) {
        when (intent) {
            is NewsIntent.LoadTopHeadlines -> loadTopHeadlines()
            is NewsIntent.SelectArticle -> navigateToDetail(intent.index)
        }
    }

    private fun loadTopHeadlines() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            //intentional delay
//            delay(5000)
            try {
                when (val result = getTopHeadlinesUseCase("us")) {
                    is com.example.news.domain.model.Result.Success -> {
                        _state.value = _state.value.copy(
                            articles = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is com.example.news.domain.model.Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                        _effect.emit(NewsEffect.ShowError(result.message))
                    }
                    is com.example.news.domain.model.Result.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
                _effect.emit(NewsEffect.ShowError(e.message ?: "Unknown error occurred"))
            }
        }
    }

    private fun networkStatus() {
        viewModelScope.launch {
            internetStatusUseCase().collect { isOnline ->
                _state.value = _state.value.copy(isOnline = isOnline)
            }
        }
    }

    private fun navigateToDetail(index: Int) {
        viewModelScope.launch {
            _effect.emit(NewsEffect.NavigateToDetail(index))
        }
    }
}
