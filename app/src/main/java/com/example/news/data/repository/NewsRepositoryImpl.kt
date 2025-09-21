package com.example.news.data.repository

import com.example.news.data.local.ArticleDao
import com.example.news.data.remote.NewsApi
import com.example.news.data.remote.VideoNewsApi
import com.example.news.domain.model.Article
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val videoNewsApi: VideoNewsApi,
    private val apiKey: String,
    private val dao: ArticleDao
) : NewsRepository {
    override suspend fun getTopHeadlines(): Result<List<Article>> {
        return try {
            Timber.d("Fetching headlines from NewsApi and VideoNewsApi")

            val articles = coroutineScope {
                val newsDeferred = async {
                    newsApi.getTopHeadlines(country = "us", apiKey = apiKey)
                        .articles.map { it.toDomain() }
                }
                val videoDeferred = async {
                    videoNewsApi.getTopHeadlines()
                        .articles.map { it.toDomain() }
                }

                interleave(newsDeferred.await(), videoDeferred.await())
            }

            dao.insertArticle(articles)
            Timber.i("Successfully fetched merged feed: ${articles.size} items")

            Result.Success(articles)

        } catch (e: Exception) {
            Timber.e("Error fetching headlines: ${e.message}")

            val cached = dao.getAllArticles().firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Timber.w("Returning cached articles: ${cached.size} items")
                Result.Success(cached)
            } else {
                Result.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
    private fun interleave(list1: List<Article>, list2: List<Article>): List<Article> {
        val merged = mutableListOf<Article>()
        val maxSize = maxOf(list1.size, list2.size)

        for (i in 0 until maxSize) {
            if (i < list1.size) merged.add(list1[i])
            if (i < list2.size) merged.add(list2[i])
        }
        return merged
    }
}
