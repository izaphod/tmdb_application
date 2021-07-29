package com.example.tmdbapplication.data.repository

import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.MovieListResponse
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MovieDataSourceImpl @Inject constructor(
    private val movieApiService: MovieApiService
) : MovieDataSource {

    override fun fetchMovies(page: Int): Single<MovieListResponse> {
        return movieApiService.getMovies(page)
    }
}