package com.example.news.data.local

import okhttp3.Interceptor
import timber.log.Timber

open class CachedData() {
    // Show cached data when offline (last 24 hours)
    val offlineCacheInterceptor = Interceptor { chain ->
        val request = chain.request()
        Timber.d("OfflineCacheInterceptor - Request headers: ${request.headers}")
        try {
            val response = chain.proceed(request)
            Timber.d("OfflineCacheInterceptor - Response headers: ${response.headers}")
            response
        } catch (e: Exception) {
            Timber.w(e, "Network failed → using cached data")
            val newRequest = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24)
                .build()

            val response = chain.proceed(newRequest)
            response
        }
    }

    // Force cache when online (1 minutes)
    val onlineCacheInterceptor = Interceptor { chain ->
        val request = chain.request()
        Timber.d("OnlineCacheInterceptor - Request headers: ${request.headers}")
        val response = chain.proceed(request)
        Timber.d("OnlineCacheInterceptor - Response headers: ${response.headers}")
        response.newBuilder()
            .header("Cache-Control", "public, max-age=" + 60 * 1) // cache 1 min
            .removeHeader("Pragma")
            .build()

    }
}