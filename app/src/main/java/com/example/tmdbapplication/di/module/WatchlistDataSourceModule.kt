package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.database.MovieDatabase
import com.example.tmdbapplication.data.repository.WatchlistDataSourceImpl
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MovieDatabaseModule::class])
class WatchlistDataSourceModule {

    @Provides
    @Singleton
    fun provideWatchlistDataSource(movieDatabase: MovieDatabase) : WatchlistDataSource {
        return WatchlistDataSourceImpl(movieDatabase)
    }
}