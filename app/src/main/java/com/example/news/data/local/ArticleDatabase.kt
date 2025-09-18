package com.example.news.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.news.domain.model.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getDatabase(context: Context): ArticleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
