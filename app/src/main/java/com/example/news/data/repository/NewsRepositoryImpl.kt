package com.example.news.data.repository

import com.example.news.data.remote.NewsApi
import com.example.news.domain.model.Article
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val api: NewsApi,
    private val apiKey: String
) : NewsRepository {
    override suspend fun getTopHeadlines(country: String): Result<List<Article>> {
        return try {
            val response = api.getTopHeadlines(country, apiKey)
            Result.Success(response.articles.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }
}
