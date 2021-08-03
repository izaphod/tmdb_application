package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.paging.MoviePagingSource
import com.example.tmdbapplication.data.paging.SearchPagingSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MovieApiModule::class])
class PagingSourceModule {

    @Provides
    @Singleton
    fun provideMoviePagingSource(movieApiService: MovieApiService): MoviePagingSource {
        return MoviePagingSource(movieApiService)
    }

    @Provides
    @Singleton
    fun provideSearchPagingSource(movieApiService: MovieApiService): SearchPagingSource {
        return SearchPagingSource(movieApiService)
    }
}