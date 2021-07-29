package com.example.tmdbapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tmdbapplication.data.database.dao.WatchlistDao
import com.example.tmdbapplication.data.database.entity.WatchlistEntity

@Database(entities = [WatchlistEntity::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun watchlistDao(): WatchlistDao
}