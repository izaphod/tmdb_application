package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.repository.Repository
import com.example.tmdbapplication.database.MovieDatabase
import com.example.tmdbapplication.network.MovieApiService
import com.example.tmdbapplication.repository.MovieRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MovieApiModule::class, MovieDatabaseModule::class])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieApiService: MovieApiService,
        movieDatabase: MovieDatabase
    ): Repository =
        MovieRepository(movieApiService, movieDatabase)
}