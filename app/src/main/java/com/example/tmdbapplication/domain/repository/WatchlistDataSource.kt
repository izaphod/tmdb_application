package com.example.tmdbapplication.domain.repository

import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface WatchlistDataSource {

    fun insert(watchlistEntity: WatchlistEntity): Completable

    fun delete(movieId: Long): Completable

    fun getWatchlist(): Observable<List<WatchlistEntity>>

    fun isInWatchlist(movieId: Long): Single<Boolean>
}