package com.example.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.di.AppModule

class NewsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(
                AppModule.getTopHeadlinesUseCase,
                AppModule.getInternetStatusUseCase,
                AppModule.bookmarkRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
