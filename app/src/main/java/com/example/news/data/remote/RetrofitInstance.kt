package com.example.news.data.remote

import com.example.news.data.remote.api.NewsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api: NewsApi by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}