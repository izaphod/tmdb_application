package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.repository.WatchlistDataSource

class IsInWatchlistUseCase {

    suspend fun execute(watchlistDataSource: WatchlistDataSource, movieId: Long): Boolean {
        return watchlistDataSource
            .isInWatchlist(movieId)
    }
}