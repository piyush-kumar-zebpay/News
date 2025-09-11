package com.example.news.data.local

import okhttp3.Interceptor
import timber.log.Timber

open class CachedData() {
    // Show cached data when offline (last 24 hours)
    val offlineCacheInterceptor = Interceptor { chain ->
        try {
            Timber.d("OfflineCacheInterceptor → proceeding normally")
            chain.proceed(chain.request())
        } catch (e: Exception) {
            Timber.w(e, "Network failed → using cached data")
            val newRequest = chain.request().newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24)
                .build()
            chain.proceed(newRequest)
        }
    }

    // Force cache when online (5 minutes)
    val onlineCacheInterceptor = Interceptor { chain ->
        Timber.d("OnlineCacheInterceptor → forcing 5 min cache")
        val response = chain.proceed(chain.request())
        response.newBuilder()
            .header("Cache-Control", "public, max-age=" + 60 * 5) // cache 5 min
            .removeHeader("Pragma") // some servers add no-cache pragma
            .build()
    }
}