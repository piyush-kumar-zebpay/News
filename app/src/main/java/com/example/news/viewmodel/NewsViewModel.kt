package com.example.news.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.model.Article
import com.example.news.repository.Datastore
import com.example.news.repository.RetrofitInstance
import com.example.news.utils.Constants.Companion.API_KEY
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val newsApi = RetrofitInstance.api
    private val datastore = Datastore(application)

    var articles by mutableStateOf<List<Article>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set


    val countryCode = datastore.countryCodeFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        "in"
    )
    init {
        // Whenever countryCode changes → fetch news
        viewModelScope.launch {
            countryCode.collectLatest { code ->
                if (!code.isNullOrBlank()) {
                    fetchNews(code, API_KEY)
                }
            }
        }
    }

    fun saveCountryCode(code: String) {
        viewModelScope.launch {
            datastore.saveCode(code)
        }
    }

    fun fetchNews(country: String, apiKey: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = newsApi.getTopHeadlines(country, apiKey)
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    articles = newsResponse?.articles ?: emptyList()
                } else {
                    errorMessage = "Network error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            }
            isLoading = false
        }
    }
}
