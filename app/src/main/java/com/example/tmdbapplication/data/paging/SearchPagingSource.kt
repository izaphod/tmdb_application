package com.example.tmdbapplication.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.MovieListResponse
import com.example.tmdbapplication.data.network.model.MovieResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SearchPagingSource @Inject constructor(
    private val movieApiService: MovieApiService
) : RxPagingSource<Int, MovieResponse>() {

    private var query = ""

    override fun getRefreshKey(state: PagingState<Int, MovieResponse>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, MovieResponse>> {
        val page = params.key ?: 1

        return movieApiService.searchMovies(query, page)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, page) }
    }

    fun setQuery(newQuery: String) {
        query = newQuery
    }

    private fun toLoadResult(data: MovieListResponse, page: Int): LoadResult<Int, MovieResponse> {
        return LoadResult.Page(
            data = data.movies,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (page == data.totalPages) null else page + 1
        )
    }
}