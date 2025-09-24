package com.example.news.data.repository

import android.content.Context
import com.example.news.data.local.articleListDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.news.data.model.Bookmarks
import com.example.news.domain.model.Article
import com.example.news.domain.repository.BookmarksRepository

class BookmarksRepository(private val context: Context): BookmarksRepository {

    override val bookmarksFlow: Flow<List<Bookmarks.Article>> =
        context.articleListDataStore.data.map { it.articlesList }

    fun domainToProto(article:Article): Bookmarks.Article {
        return Bookmarks.Article.newBuilder()
            .setTitle(article.title)
            .setAuthor(article.author ?: "")
            .setDescription(article.description ?: "")
            .setUrl(article.url)
            .setImageUrl(article.imageUrl ?: "")
            .setPublishedAt(article.publishedAt)
            .setContent(article.content ?: "")
            .setSource(
                Bookmarks.SourceDto.newBuilder()
                    .setId(article.sourceId ?: "")
                    .setName(article.sourceName)
                    .build()
            )
            .build()
    }

    override suspend fun addBookmark(article: Article) {
        val protoArticle = domainToProto(article) // convert to proto
        context.articleListDataStore.updateData { current ->
            current.toBuilder()
                .addArticles(protoArticle)
                .build()
        }
    }

    override suspend fun removeBookmark(articleUrl: String) {
        context.articleListDataStore.updateData { current ->
            val updatedArticles = current.articlesList.filter { it.url != articleUrl }
            current.toBuilder()
                .clearArticles()
                .addAllArticles(updatedArticles)
                .build()
        }
    }
}
