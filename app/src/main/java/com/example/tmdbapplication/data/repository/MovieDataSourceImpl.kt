package com.example.tmdbapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.observable
import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.MovieResponse
import com.example.tmdbapplication.data.pager.MoviePagingSource
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MovieDataSourceImpl @Inject constructor(
    private val moviePagingSource: MoviePagingSource,
    private val movieApiService: MovieApiService
) : MovieDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMoviesByPage(): Observable<PagingData<MovieResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                maxSize = 30,
                prefetchDistance = 5,
                initialLoadSize = 40
            ),
            pagingSourceFactory = { moviePagingSource }
        ).observable
    }

    override fun getMovieById(movieId: Long): Single<MovieResponse> {
        return movieApiService.getMovieById(movieId)
    }
}