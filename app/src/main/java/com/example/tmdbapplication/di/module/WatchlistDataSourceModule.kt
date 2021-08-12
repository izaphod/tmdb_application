package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.database.MovieDatabase
import com.example.tmdbapplication.data.repository.WatchlistDataSourceImpl
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [MovieDatabaseModule::class])
object WatchlistDataSourceModule {

    @Provides
    @Singleton
    fun provideWatchlistDataSource(movieDatabase: MovieDatabase) : WatchlistDataSource {
        return WatchlistDataSourceImpl(movieDatabase)
    }
}