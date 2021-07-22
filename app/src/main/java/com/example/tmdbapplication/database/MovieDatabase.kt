package com.example.tmdbapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tmdbapplication.database.dao.MovieDao
import com.example.tmdbapplication.database.dao.WatchlistDao
import com.example.tmdbapplication.database.entity.MovieEntity
import com.example.tmdbapplication.database.entity.WatchlistEntity

@Database(entities = [MovieEntity::class, WatchlistEntity::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun watchlistDao(): WatchlistDao
}