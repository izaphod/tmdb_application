package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(
    private val movieDataSource: MovieDataSource
) {
    suspend fun execute(movieId: Long): Movie {
        return movieDataSource.getMovieById(movieId)
    }
}