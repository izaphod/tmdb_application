package com.example.tmdbapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface WatchlistDataSource {

    suspend fun insert(movieId: Long)

    suspend fun delete(movieId: Long)

    fun getWatchlist(): Flow<List<Long>>

    suspend fun isInWatchlist(movieId: Long): Boolean
}