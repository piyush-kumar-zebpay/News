package com.example.news.domain.usecase

import com.example.news.data.utils.NetworkStatusTrackerImpl
import kotlinx.coroutines.flow.Flow

class GetInternetStatusUseCase(
    private val tracker: NetworkStatusTrackerImpl
) {
    suspend operator fun invoke(): Flow<Boolean> {
        return tracker.observe()
    }
}