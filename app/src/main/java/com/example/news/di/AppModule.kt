package com.example.news.di

import com.example.news.data.local.ArticleDatabase
import com.example.news.data.remote.NewsApi
import com.example.news.data.remote.VideoNewsApi
import com.example.news.data.repository.NewsRepositoryImpl
import com.example.news.domain.repository.NewsRepository
import com.example.news.domain.usecase.GetInternetStatusUseCase
import com.example.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.news.data.utils.NetworkStatusTrackerImpl
import com.example.news.data.repository.BookmarksRepository
import com.example.news.domain.repository.NetworkStatusTracker
import com.example.news.presentation.viewmodel.NewsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object AppModule {
    private const val API_KEY = "373defa50545436bbc2c603ed356fb6d"

    fun getModules() = module {
        single {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = false; isLenient = false })
                }
            }
        }
        singleOf(::NewsApi)
        singleOf(::VideoNewsApi)


        single { ArticleDatabase.getDatabase(androidContext()) }
        single { get<ArticleDatabase>().articleDao() }


        single<NewsRepository> { NewsRepositoryImpl(
            get(), get(), API_KEY, get(), context = androidContext()
        ) }


        singleOf(::GetTopHeadlinesUseCase)
        singleOf(::GetInternetStatusUseCase)

        singleOf(::NetworkStatusTrackerImpl) { bind<NetworkStatusTracker>() }

        single { BookmarksRepository(androidContext()) }

        viewModel {
            NewsViewModel(
                get(),
                get(),
                get()
            )
        }
    }
}
