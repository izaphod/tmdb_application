package com.example.tmdbapplication.domain.usecase

import androidx.annotation.WorkerThread
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import javax.inject.Inject

class DeleteFromWatchlistUseCase @Inject constructor(
    private val watchlistDataSource: WatchlistDataSource
) {

    @WorkerThread
    suspend fun execute(movieId: Long) {
        return watchlistDataSource
            .delete(movieId)
    }
}