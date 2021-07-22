package com.example.tmdbapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdbapplication.database.entity.WatchlistEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(watchlistEntity: WatchlistEntity): Completable

    @Query("SELECT * FROM watchlist_database ORDER BY id ASC")
    fun getWatchlist(): Observable<List<WatchlistEntity>>

    @Query("DELETE FROM watchlist_database WHERE movieId = :movieId")
    fun deleteMovie(movieId: Long): Completable

    @Query("SELECT EXISTS(SELECT * FROM watchlist_database WHERE movieId = :movieId)")
    fun isInWatchlist(movieId: Long): Single<Boolean>
}