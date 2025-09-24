package com.example.news.data.remote

import com.example.news.data.model.NewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsApi(private val client: HttpClient) {
    suspend fun getTopHeadlines(country: String = "us", apiKey : String): NewsResponseDto {
        return client.get("https://newsapi.org/v2/top-headlines") {
            parameter("country", country)
            parameter("apiKey", apiKey)
        }.body()
    }
}
