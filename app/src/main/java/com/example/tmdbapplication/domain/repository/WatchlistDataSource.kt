package com.example.tmdbapplication.domain.repository

interface WatchlistDataSource {

    suspend fun insert(movieId: Long)

    suspend fun delete(movieId: Long)

    suspend fun getWatchlist(): List<Long>

    suspend fun isInWatchlist(movieId: Long): Boolean
}