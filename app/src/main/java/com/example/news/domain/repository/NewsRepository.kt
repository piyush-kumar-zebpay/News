package com.example.news.domain.repository

import com.example.news.domain.model.Article
import com.example.news.domain.model.Result

interface NewsRepository {
    suspend fun getTopHeadlines(country: String): Result<List<Article>>
}
