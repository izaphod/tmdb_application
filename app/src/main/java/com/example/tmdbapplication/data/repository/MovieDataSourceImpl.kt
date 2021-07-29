package com.example.tmdbapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.observable
import com.example.tmdbapplication.data.network.model.MovieResponse
import com.example.tmdbapplication.data.pager.MoviePagingSource
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MovieDataSourceImpl @Inject constructor(
    private val moviePagingSource: MoviePagingSource
) : MovieDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchMovies(): Observable<PagingData<MovieResponse>> {
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
}