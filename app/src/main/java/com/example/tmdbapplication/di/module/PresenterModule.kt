package com.example.tmdbapplication.di.module

import com.example.tmdbapplication.presentation.movielists.MovieListsPresenter
import com.example.tmdbapplication.repository.Repository
import dagger.Module
import dagger.Provides

@Module(includes = [RepositoryModule::class])
class PresenterModule {

    @Provides
    fun provideMovieListPresenter(movieRepository: Repository): MovieListsPresenter =
        MovieListsPresenter(movieRepository)
}