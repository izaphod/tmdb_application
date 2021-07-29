package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.presentation.movielists.MovieListPresenter
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import dagger.Module
import dagger.Provides

@Module(includes = [MovieDataSourceModule::class, WatchlistDataSourceModule::class])
class PresenterModule {

    @Provides
    fun provideMovieListPresenter(
        movieMovieDataSource: MovieDataSource,
        watchlistDataSource: WatchlistDataSource
    ): MovieListPresenter = MovieListPresenter(movieMovieDataSource, watchlistDataSource)
}