package com.example.tmdbapplication.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tmdbapplication.domain.model.Movie

@Entity(tableName = "watchlist_database")
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "movieId") val movieId: Long
)
