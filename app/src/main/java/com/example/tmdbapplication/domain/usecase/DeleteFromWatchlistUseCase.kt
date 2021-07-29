package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class DeleteFromWatchlistUseCase {

    fun execute(watchlistDataSource: WatchlistDataSource, movieId: Long): Completable {
        return watchlistDataSource.delete(movieId)
            .subscribeOn(Schedulers.io())
    }
}