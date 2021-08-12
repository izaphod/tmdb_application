package com.example.tmdbapplication.domain.usecase

import androidx.annotation.WorkerThread
import com.example.tmdbapplication.domain.repository.WatchlistDataSource

class DeleteFromWatchlistUseCase {

    @WorkerThread
    suspend fun execute(watchlistDataSource: WatchlistDataSource, movieId: Long) {
        return watchlistDataSource
            .delete(movieId)
    }
}