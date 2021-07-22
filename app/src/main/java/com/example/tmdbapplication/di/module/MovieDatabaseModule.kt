package com.example.tmdbapplication.di.module

import android.content.Context
import androidx.room.Room
import com.example.tmdbapplication.database.MovieDatabase
import com.example.tmdbapplication.database.dao.MovieDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MovieDatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(context: Context): MovieDatabase = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        "movie-db"
    ).build()

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao = database.movieDao()
}