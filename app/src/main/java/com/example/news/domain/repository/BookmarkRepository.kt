package com.example.news.domain.repository

import android.content.Context
import com.example.news.data.local.bookmarksDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarksRepository(private val context: Context) {

    val bookmarksFlow: Flow<List<String>> =
        context.bookmarksDataStore.data.map { it.itemsList }

    suspend fun addBookmark(item: String) {
        context.bookmarksDataStore.updateData { current ->
            current.toBuilder()
                .addItems(item)
                .build()
        }
    }

    suspend fun removeBookmark(item: String) {
        context.bookmarksDataStore.updateData { current ->
            current.toBuilder()
                .clearItems()
                .addAllItems(current.itemsList.filter { it != item })
                .build()
        }
    }
}
