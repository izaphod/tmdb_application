package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.pager.MoviePagingSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MovieApiModule::class])
class MoviePagingSourceModule {

    @Provides
    @Singleton
    fun provideMoviePagingSource(movieApiService: MovieApiService): MoviePagingSource {
        return MoviePagingSource(movieApiService)
    }
}