package com.example.news.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "articles",  indices = [Index(value = ["url"], unique = true)])
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceId: String?,
    val sourceName: String?,
    val author: String?,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "url")
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val content: String?
)




