package com.example.news.data.remote

import com.example.news.data.model.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoNewsApi {
    @GET("api/user")
    suspend fun getTopHeadlines(): NewsResponseDto
}
