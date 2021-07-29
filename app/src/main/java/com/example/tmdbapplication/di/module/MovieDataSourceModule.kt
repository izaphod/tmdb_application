package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.data.pager.MoviePagingSource
import com.example.tmdbapplication.data.repository.MovieDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MoviePagingSourceModule::class, MovieApiModule::class])
class MovieDataSourceModule {

    @Provides
    @Singleton
    fun provideMovieDataSource(
        moviePagingSource: MoviePagingSource,
        movieApiService: MovieApiService
    ): MovieDataSource = MovieDataSourceImpl(moviePagingSource, movieApiService)
}