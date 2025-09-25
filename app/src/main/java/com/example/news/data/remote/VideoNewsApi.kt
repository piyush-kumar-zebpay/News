package com.example.news.data.remote

import com.example.news.data.model.NewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class VideoNewsApi(private val client: HttpClient){
    suspend fun getTopHeadlines(): NewsResponseDto{
        return client.get("https://videonews.free.beeceptor.com/api/user"){
        }.body()
    }
}
