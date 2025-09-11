package com.example.news

import android.app.Application
import timber.log.Timber
import com.example.news.di.AppModule

class NewsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            AppModule.init(this)
        }
    }
}