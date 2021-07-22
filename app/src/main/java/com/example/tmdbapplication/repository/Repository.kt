package com.example.tmdbapplication.repository

import com.example.tmdbapplication.database.entity.MovieEntity
import com.example.tmdbapplication.database.entity.WatchlistEntity
import com.example.tmdbapplication.network.model.MovieResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface Repository {

    fun onFirstAppOpened(): Observable<List<MovieEntity>>

    fun getMovies(page: Int): Observable<List<MovieEntity>>

    fun onWatchlistPressed(watchlistEntity: WatchlistEntity, isInWatchlist: Boolean)

    fun onWatchlistOpened(): Single<List<MovieResponse>>

    fun isInWatchlist(movieId: Long): Single<Boolean>
}