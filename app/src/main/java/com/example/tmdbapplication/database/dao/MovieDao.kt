package com.example.tmdbapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdbapplication.database.entity.MovieEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(movieEntities: List<MovieEntity>): Completable

    @Query("SELECT * FROM movie_database WHERE page = :page ORDER BY id ASC")
    fun getMovies(page: Int): Observable<List<MovieEntity>>

    @Query("DELETE FROM movie_database")
    fun clearMovies(): Completable
}
