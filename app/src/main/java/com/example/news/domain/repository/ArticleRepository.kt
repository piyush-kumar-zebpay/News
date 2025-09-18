package com.example.news.domain.repository


import com.example.news.data.local.ArticleDao
import com.example.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

class ArticleRepository(
    private val dao: ArticleDao
) {
    fun getAllArticles(): Flow<List<Article>> = dao.getAllArticles()

    suspend fun insertArticle(articles: List<Article>) {
        dao.insertArticle(articles)
    }

//    suspend fun deleteArticle(article: Article) {
//        dao.deleteArticle(article)
//    }

}
