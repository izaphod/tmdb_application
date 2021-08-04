package com.example.tmdbapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.observable
import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.asDomainModel
import com.example.tmdbapplication.data.paging.MoviePagingSource
import com.example.tmdbapplication.data.paging.SearchPagingSource
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MovieDataSourceImpl @Inject constructor(
    private val movieApiService: MovieApiService
) : MovieDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMoviesByPage(): Observable<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                maxSize = 30,
                prefetchDistance = 5,
                initialLoadSize = 40
            ),
            pagingSourceFactory = { MoviePagingSource(movieApiService) }
        )
            .observable
            .map { pagingData -> pagingData.map { movieResponse -> movieResponse.asDomainModel() } }
    }

    override fun getMovieById(movieId: Long): Single<Movie> {
        return movieApiService.getMovieById(movieId)
            .map { it.asDomainModel() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchMovie(query: String): Observable<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                maxSize = 30,
                prefetchDistance = 5,
                initialLoadSize = 40
            ),
            pagingSourceFactory = { SearchPagingSource(movieApiService, query) }
        )
            .observable
            .map { pagingData -> pagingData.map { movieResponse -> movieResponse.asDomainModel() } }
    }
}