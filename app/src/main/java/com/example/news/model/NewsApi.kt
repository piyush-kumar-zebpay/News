package com.example.news.model

import com.example.news.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
        @GET("v2/top-headlines")
        suspend fun getTopHeadlines(
            @Query("country") countryCode: String = "us",
            @Query("apiKey") apiKey: String = API_KEY
        ): Response<NewsResponse>
}