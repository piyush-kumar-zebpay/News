package com.example.news

import android.app.Application
//import com.example.news.data.local.DatastoreProto
import timber.log.Timber
import com.example.news.di.AppModule
import com.example.news.presentation.viewmodel.NewsViewModel

class NewsApplication: Application() {
//    lateinit var datastoreProto: DatastoreProto
//        private set

    override fun onCreate() {
        super.onCreate()
//        datastoreProto = DatastoreProto(this)
        AppModule.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}