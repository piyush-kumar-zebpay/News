package com.example.news.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.news.data.model.Bookmarks

val Context.articleListDataStore: DataStore<Bookmarks.ArticleList> by dataStore(
    fileName = "articles.pb",
    serializer = ArticleListSerializer
)
