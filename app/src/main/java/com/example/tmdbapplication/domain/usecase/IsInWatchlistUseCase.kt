package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class IsInWatchlistUseCase {
    fun execute(watchlistDataSource: WatchlistDataSource, movieId: Long): Single<Boolean> {
        return watchlistDataSource.isInWatchlist(movieId)
            .subscribeOn(Schedulers.io())
    }
}