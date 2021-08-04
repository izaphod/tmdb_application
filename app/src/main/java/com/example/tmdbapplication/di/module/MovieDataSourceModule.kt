package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.data.repository.MovieDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MovieApiModule::class])
class MovieDataSourceModule {

    @Provides
    @Singleton
    fun provideMovieDataSource(movieApiService: MovieApiService): MovieDataSource =
        MovieDataSourceImpl(movieApiService)
}