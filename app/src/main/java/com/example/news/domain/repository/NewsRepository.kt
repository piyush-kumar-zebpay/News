package com.example.news.domain.repository

import com.example.news.domain.model.HeadlinesResult
import com.example.news.domain.model.Result

interface NewsRepository {
    suspend fun getTopHeadlines(): Result<HeadlinesResult>
}
