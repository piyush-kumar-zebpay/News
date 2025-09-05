package com.example.news.data.remote.api

import com.example.news.data.remote.model.NewsResponse
import com.example.news.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface
NewsApi {
        @GET("v2/top-headlines")
        suspend fun getTopHeadlines(
            @Query("country") countryCode: String = "us",
            @Query("apiKey") apiKey: String = Constants.Companion.API_KEY
        ): Response<NewsResponse>
}