package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import javax.inject.Inject

class IsInWatchlistUseCase @Inject constructor(
    private val watchlistDataSource: WatchlistDataSource
) {

    suspend fun execute(movieId: Long): Boolean {
        return watchlistDataSource
            .isInWatchlist(movieId)
    }
}