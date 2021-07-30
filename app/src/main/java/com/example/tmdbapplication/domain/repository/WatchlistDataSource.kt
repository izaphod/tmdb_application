package com.example.tmdbapplication.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface WatchlistDataSource {

    fun insert(movieId: Long): Completable

    fun delete(movieId: Long): Completable

    fun getWatchlist(): Observable<List<Long>>

    fun isInWatchlist(movieId: Long): Single<Boolean>
}