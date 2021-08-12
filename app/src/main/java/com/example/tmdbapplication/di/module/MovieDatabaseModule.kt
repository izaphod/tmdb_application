package com.example.tmdbapplication.di.module

import android.content.Context
import androidx.room.Room
import com.example.tmdbapplication.data.database.MovieDatabase
import com.example.tmdbapplication.data.database.dao.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MovieDatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase =
        Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie-db"
        ).build()

    @Provides
    @Singleton
    fun provideWatchlistDao(database: MovieDatabase): WatchlistDao = database.watchlistDao()
}