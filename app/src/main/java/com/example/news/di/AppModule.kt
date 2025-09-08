package com.example.news.di

import com.example.news.data.remote.NewsApi
import com.example.news.data.repository.NewsRepositoryImpl
import com.example.news.domain.repository.NewsRepository
import com.example.news.domain.usecase.GetTopHeadlinesUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    private const val API_KEY = "373defa50545436bbc2c603ed356fb6d"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: NewsApi by lazy {
        retrofit.create(NewsApi::class.java)
    }

    private val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(api, API_KEY)
    }

    val getTopHeadlinesUseCase: GetTopHeadlinesUseCase by lazy {
        GetTopHeadlinesUseCase(newsRepository)
    }
}
