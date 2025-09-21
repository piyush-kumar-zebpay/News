package com.example.news.presentation.util

enum class MediaType {
    VIDEO,
    IMAGE;

    companion object {
        fun fromUrl(url: String): MediaType {
            return if (url.lowercase().endsWith(".mp4") ||
                url.lowercase().endsWith(".mkv") ||
                url.lowercase().endsWith(".webm")
            ) {
                VIDEO
            } else {
                IMAGE
            }
        }
    }
}
