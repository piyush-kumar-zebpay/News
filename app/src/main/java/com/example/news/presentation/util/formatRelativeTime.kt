package com.example.news.presentation.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun formatRelativeTime(isoTime: String?): String {
    if (isoTime.isNullOrEmpty()) return ""

    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val publishedDate = sdf.parse(isoTime) ?: return ""

        val now = Date()
        var diffInMillis = now.time - publishedDate.time

        if (diffInMillis < 0) diffInMillis = 0 // avoid negative times

        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        diffInMillis -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        diffInMillis -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

        buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (isEmpty()) append("now")
            else append("ago")
        }.trim()
    } catch (e: Exception) {
        ""
    }
}
