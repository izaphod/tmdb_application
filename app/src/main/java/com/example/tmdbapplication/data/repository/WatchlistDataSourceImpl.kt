package com.example.tmdbapplication.data.repository

import com.example.tmdbapplication.data.database.MovieDatabase
import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class WatchlistDataSourceImpl @Inject constructor(
    private val movieDatabase: MovieDatabase
) : WatchlistDataSource {

    override fun insert(movieId: Long): Completable {
        return movieDatabase.watchlistDao().insertMovie(WatchlistEntity(movieId = movieId))
    }

    override fun delete(movieId: Long): Completable {
        return movieDatabase.watchlistDao().deleteMovie(movieId)
    }

    override fun getWatchlist(): Observable<List<Long>> {
        return movieDatabase.watchlistDao().getWatchlist()
            .map { it.map { watchlistEntity -> watchlistEntity.movieId } }
    }

    override fun isInWatchlist(movieId: Long): Single<Boolean> {
        return movieDatabase.watchlistDao().isInWatchlist(movieId)
    }
}