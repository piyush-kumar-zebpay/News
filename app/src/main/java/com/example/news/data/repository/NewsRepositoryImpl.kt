package com.example.news.data.repository

import com.example.news.data.local.ArticleDao
import com.example.news.data.remote.NewsApi
import com.example.news.domain.model.Article
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class NewsRepositoryImpl(
    private val api: NewsApi,
    private val apiKey: String,
    private val dao: ArticleDao
) : NewsRepository {
    override suspend fun getTopHeadlines(country: String): Result<List<Article>> {
        return try {
            Timber.d("Fetching headlines")
            val response = api.getTopHeadlines(country, apiKey)
            val articles = response.articles.map { it.toDomain() }
            dao.insertArticle(articles)
            Timber.i("Successfully fetched headlines ${response.articles.size}")
            Result.Success(articles)
        } catch (e: Exception) {
            Timber.e("Error fetching headlines ${e.message}")
            val cached = dao.getAllArticles()
                .firstOrNull()

            if (!cached.isNullOrEmpty()) {
                Timber.w("Returning cached articles ${cached.size}")
                Result.Success(cached)
            } else {
                Result.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
