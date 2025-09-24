package com.example.news.data.repository

import com.example.news.data.local.ArticleDao
import com.example.news.data.remote.NewsApi
import com.example.news.data.remote.VideoNewsApi
import com.example.news.domain.model.HeadlinesResult
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
    override suspend fun getTopHeadlines(): Result<HeadlinesResult> {
        return try {
            Timber.d("Fetching headlines from NewsApi and VideoNewsApi")

            coroutineScope {
                val newsArticlesDeferred = async {
                    newsApi.getTopHeadlines(country = "us", apiKey = apiKey)
                        .articles.map { it.toDomain() }
                }

                val videoArticlesDeferred = async {
                    videoNewsApi.getTopHeadlines()
                        .articles.map { it.toDomain() }
                }

                val newsArticles = newsArticlesDeferred.await()
                val videoArticles = videoArticlesDeferred.await()

                dao.insertArticle(newsArticles)

                Timber.i("Fetched news=${newsArticles.size}, videos=${videoArticles.size}")

                Result.Success(
                    HeadlinesResult(
                        newsArticles = newsArticles,
                        videoArticles = videoArticles
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e("Error fetching headlines: ${e.message}")

            val cached = dao.getAllArticles().firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Timber.w("Returning cached articles: ${cached.size} items")

                return Result.Success(
                    HeadlinesResult(
                        newsArticles = cached,
                        videoArticles = emptyList()
                    )
                )
            } else {
                return Result.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
