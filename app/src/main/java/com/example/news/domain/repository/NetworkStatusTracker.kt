package com.example.news.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkStatusTracker {
    suspend fun observe(): Flow<Boolean>
}