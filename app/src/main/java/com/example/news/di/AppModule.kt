package com.example.news.di

import android.content.Context
import com.example.news.data.local.CachedData
import com.example.news.data.remote.NewsApi
import com.example.news.data.repository.NewsRepositoryImpl
import com.example.news.domain.repository.NewsRepository
import com.example.news.domain.usecase.GetInternetStatusUseCase
import com.example.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.news.data.utils.NetworkStatusTrackerImpl
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File

object AppModule{
    private lateinit var appContext: Context
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private const val API_KEY = "373defa50545436bbc2c603ed356fb6d"

    private val cachedData: CachedData by lazy {
        CachedData()
    }

    private val okHttpClient: OkHttpClient by lazy {
        Timber.d("Building OkHttpClient with cache + interceptors")
        OkHttpClient.Builder()
            .cache(Cache(File(appContext.cacheDir, "http_cache"), 10L * 1024 * 1024)) // 10 MB
            .addInterceptor(cachedData.offlineCacheInterceptor)
            .addNetworkInterceptor(cachedData.onlineCacheInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                Timber.d("HttpLoggingInterceptor set to BODY level")
            })
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Timber.d("Initializing Retrofit")
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NewsApi by lazy {
        retrofit.create(NewsApi::class.java)
    }

    private val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(api, API_KEY)
    }

    val getTopHeadlinesUseCase: GetTopHeadlinesUseCase by lazy {
        GetTopHeadlinesUseCase(newsRepository)
    }

    private val networkStatusTracker: NetworkStatusTrackerImpl by lazy {
        NetworkStatusTrackerImpl(appContext)
    }
    val getInternetStatusUseCase: GetInternetStatusUseCase by lazy {
        GetInternetStatusUseCase(networkStatusTracker)
    }

}
