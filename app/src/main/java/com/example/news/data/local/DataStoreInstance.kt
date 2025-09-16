package com.example.news.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.news.data.model.BookmarksOuterClass

private const val BOOKMARKS_FILE_NAME = "bookmarks.pb"

val Context.bookmarksDataStore: DataStore<BookmarksOuterClass.Bookmarks> by dataStore(
    fileName = BOOKMARKS_FILE_NAME,
    serializer = BookmarksSerializer
)
