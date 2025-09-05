package com.example.news.data.repository

import com.example.news.data.remote.RetrofitInstance
import com.example.news.data.remote.model.NewsResponse
import retrofit2.Response

class NewsRepository {
    suspend fun getTopHeadlines(country: String, apiKey: String): Response<NewsResponse> {
        return RetrofitInstance.Companion.api.getTopHeadlines(country, apiKey)
    }
}