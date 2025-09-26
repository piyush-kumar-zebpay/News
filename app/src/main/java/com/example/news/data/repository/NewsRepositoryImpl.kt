package com.example.news.data.repository

import android.content.Context
import com.example.news.data.local.ArticleDao
import com.example.news.data.local.articleListDataStore
import com.example.news.data.model.Bookmarks
import com.example.news.data.remote.NewsApi
import com.example.news.data.remote.VideoNewsApi
import com.example.news.domain.model.Article
import com.example.news.domain.model.HeadlinesResult
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val videoNewsApi: VideoNewsApi,
    private val apiKey: String,
    private val dao: ArticleDao,
    private val context: Context
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

//                dao.insertArticle(newsArticles)

                val storedArticles = context.articleListDataStore.data.firstOrNull()?.articlesList ?: emptyList()
                val isBookmarked = (newsArticles + videoArticles).any { it.url in storedArticles.map { a -> a.url } }

                fun Bookmarks.Article.toDomainArticle(): Article {
                    return Article(
                        url = url,
                        title = title,
                        description = description,
                        author = author,
                        imageUrl = imageUrl,
                        publishedAt = publishedAt,
                        content = content,
                        sourceId = source.id,
                        sourceName = source.name,
                        isBookmarked = true
                    )
                }

                val observeBookmarks: Flow<List<Article>> =
                    context.articleListDataStore.data.map { bookmarks ->
                        bookmarks.articlesList.map { it.toDomainArticle() }
                    }



                Timber.i("Fetched news=${newsArticles.size}, videos=${videoArticles.size}")

                Result.Success(
                    HeadlinesResult(
                        newsArticles = newsArticles,
                        videoArticles = videoArticles,
                        isBookmarked = isBookmarked,
                        observeBookmarks = observeBookmarks
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e("Error fetching headlines: ${e.message}")

            val cached = dao.getAllArticles().firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Timber.w("Returning cached articles: ${cached.size} items")

                val storedArticles = context.articleListDataStore.data.firstOrNull()?.articlesList ?: emptyList()
                val isBookmarked = (cached).any { it.url in storedArticles.map { a -> a.url } }
                fun Bookmarks.Article.toDomainArticle(): Article {
                    return Article(
                        url = url,
                        title = title,
                        description = description,
                        author = author,
                        imageUrl = imageUrl,
                        publishedAt = publishedAt,
                        content = content,
                        sourceId = source.id,
                        sourceName = source.name,
                        isBookmarked = true
                    )
                }

                val observeBookmarks: Flow<List<Article>> =
                    context.articleListDataStore.data.map { bookmarks ->
                        bookmarks.articlesList.map { it.toDomainArticle() }
                    }

                return Result.Success(
                    HeadlinesResult(
                        newsArticles = cached,
                        videoArticles = emptyList(),
                        isBookmarked = isBookmarked,
                        observeBookmarks = observeBookmarks
                    )
                )
            } else {
                return Result.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
