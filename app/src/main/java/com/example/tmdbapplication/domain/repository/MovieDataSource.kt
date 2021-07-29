package com.example.tmdbapplication.domain.repository

import com.example.tmdbapplication.data.network.model.MovieListResponse
import io.reactivex.rxjava3.core.Single

interface MovieDataSource {

    fun fetchMovies(page: Int): Single<MovieListResponse>
}