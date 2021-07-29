package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Completable

class DeleteFromWatchlistUseCase {

    fun execute(watchlistDataSource: WatchlistDataSource, movieId: Long): Completable {
        return watchlistDataSource.delete(movieId)
    }
}