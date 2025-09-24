package com.example.news.domain.usecase

import com.example.news.domain.model.HeadlinesResult
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository

class GetTopHeadlinesUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(): Result<HeadlinesResult> {
        return repository.getTopHeadlines()
    }
}
