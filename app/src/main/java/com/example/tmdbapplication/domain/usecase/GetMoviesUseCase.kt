package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val movieDataSource: MovieDataSource
) {

    suspend fun executeTrending(): Flow<List<Movie>> {
        return movieDataSource.getTrendingMovies()
    }

    suspend fun executeTriple(): Flow<Triple<List<Movie>, List<Movie>, List<Movie>>> {
        return movieDataSource.getMovies()
    }
}