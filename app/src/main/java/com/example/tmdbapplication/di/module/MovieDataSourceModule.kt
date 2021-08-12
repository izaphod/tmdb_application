package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.data.repository.MovieDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [MovieApiModule::class])
object MovieDataSourceModule {

    @Provides
    @Singleton
    fun provideMovieDataSource(
        movieApiService: MovieApiService
    ): MovieDataSource = MovieDataSourceImpl( movieApiService)
}