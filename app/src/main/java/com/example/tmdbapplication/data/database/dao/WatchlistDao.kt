package com.example.tmdbapplication.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(watchlistEntity: WatchlistEntity)

    @Query("SELECT * FROM watchlist_database ORDER BY id ASC")
    fun getWatchlist(): Flow<List<WatchlistEntity>>

    @Query("DELETE FROM watchlist_database WHERE movieId = :movieId")
    suspend fun deleteMovie(movieId: Long)

    @Query("SELECT EXISTS(SELECT * FROM watchlist_database WHERE movieId = :movieId)")
    suspend fun isInWatchlist(movieId: Long): Boolean
}