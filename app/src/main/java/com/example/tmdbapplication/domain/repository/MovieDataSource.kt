package com.example.tmdbapplication.domain.repository

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieDataSource {

    suspend fun getMovies(): Flow<Triple<List<Movie>, List<Movie>, List<Movie>>>

    suspend fun getTrendingMovies(): Flow<List<Movie>>

    fun getPagedPopular(): Flow<PagingData<Movie>>

    fun getPagedNowPlaying(): Flow<PagingData<Movie>>

    fun getPagedUpcoming(): Flow<PagingData<Movie>>

    suspend fun getMovieById(movieId: Long): Movie

    fun searchMovie(query: String): Flow<PagingData<Movie>>
}