package com.example.tmdbapplication.domain.usecase

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import kotlinx.coroutines.flow.Flow

class SearchMovieUseCase {

    fun execute(movieDataSource: MovieDataSource, query: String): Flow<PagingData<Movie>> {
        return movieDataSource
            .searchMovie(query)
    }
}