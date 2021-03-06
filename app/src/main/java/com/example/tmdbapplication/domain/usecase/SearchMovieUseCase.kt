package com.example.tmdbapplication.domain.usecase

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(
    private val movieDataSource: MovieDataSource
) {

    fun execute(query: String): Flow<PagingData<Movie>> {
        return movieDataSource
            .searchMovie(query)
    }
}