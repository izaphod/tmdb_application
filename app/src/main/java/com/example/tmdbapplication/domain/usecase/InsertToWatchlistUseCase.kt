package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class InsertToWatchlistUseCase {

    fun execute(
        watchlistDataSource: WatchlistDataSource,
        movieId: Long
    ): Completable {
        return watchlistDataSource.insert(movieId)
            .subscribeOn(Schedulers.io())
    }
}