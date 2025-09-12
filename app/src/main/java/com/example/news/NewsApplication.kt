package com.example.news

import android.app.Application
import timber.log.Timber
import com.example.news.di.AppModule
import com.example.news.presentation.viewmodel.NewsViewModel

class NewsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}