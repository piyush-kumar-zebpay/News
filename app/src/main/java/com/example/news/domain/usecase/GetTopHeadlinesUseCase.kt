package com.example.news.domain.usecase

import com.example.news.data.model.ArticleDto
import com.example.news.domain.model.Article
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository

class GetTopHeadlinesUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(country: String): Result<List<Article>> {
        return repository.getTopHeadlines(country)
    }
}
