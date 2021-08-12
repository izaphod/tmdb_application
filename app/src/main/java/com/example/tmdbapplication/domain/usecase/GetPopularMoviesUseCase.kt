package com.example.tmdbapplication.domain.usecase

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import kotlinx.coroutines.flow.Flow

class GetPopularMoviesUseCase {

    fun execute(movieDataSource: MovieDataSource): Flow<PagingData<Movie>> {
        return movieDataSource.getPopularMovies()
    }
}