package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.data.network.model.MovieListResponse
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class GetMoviesUseCase {
    fun execute(movieDataSource: MovieDataSource, page: Int): Single<MovieListResponse> =
        movieDataSource.fetchMovies(page)
            .subscribeOn(Schedulers.io())
}