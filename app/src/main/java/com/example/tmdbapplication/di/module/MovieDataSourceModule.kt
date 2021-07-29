package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.data.pager.MoviePagingSource
import com.example.tmdbapplication.data.repository.MovieDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MoviePagingSourceModule::class])
class MovieDataSourceModule {

    @Provides
    @Singleton
    fun provideMovieDataSource(moviePagingSource: MoviePagingSource): MovieDataSource =
        MovieDataSourceImpl(moviePagingSource)
}