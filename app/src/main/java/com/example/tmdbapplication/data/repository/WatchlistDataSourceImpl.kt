package com.example.tmdbapplication.data.repository

import com.example.tmdbapplication.data.database.MovieDatabase
import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchlistDataSourceImpl @Inject constructor(
    private val movieDatabase: MovieDatabase
) : WatchlistDataSource {

    override suspend fun insert(movieId: Long) {
        return movieDatabase
            .watchlistDao()
            .insertMovie(WatchlistEntity(movieId = movieId))
    }

    override suspend fun delete(movieId: Long) {
        return movieDatabase
            .watchlistDao()
            .deleteMovie(movieId)
    }

    override fun getWatchlist(): Flow<List<Long>> {
        return movieDatabase
            .watchlistDao()
            .getWatchlist()
            .map { list -> list.map { watchlistEntity -> watchlistEntity.movieId } }
    }

    override suspend fun isInWatchlist(movieId: Long): Boolean {
        return movieDatabase
            .watchlistDao()
            .isInWatchlist(movieId)
    }
}