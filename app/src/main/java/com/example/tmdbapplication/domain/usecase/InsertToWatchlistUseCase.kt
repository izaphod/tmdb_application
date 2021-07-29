package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class InsertToWatchlistUseCase {

    fun execute(
        watchlistDataSource: WatchlistDataSource,
        watchlistEntity: WatchlistEntity
    ): Completable {
        return watchlistDataSource.insert(watchlistEntity)
            .subscribeOn(Schedulers.io())
    }
}