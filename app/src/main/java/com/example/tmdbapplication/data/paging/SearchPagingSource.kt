package com.example.tmdbapplication.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.asDomainModel
import com.example.tmdbapplication.domain.model.Movie
import javax.inject.Inject

class SearchPagingSource @Inject constructor(
    private val movieApiService: MovieApiService,
    private val query: String
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        try {
            val page = params.key ?: 1
            val response = movieApiService.searchMovie(query, page)
            return if (response.totalPages != 0) {
                LoadResult.Page(
                    data = response.movies.asDomainModel(),
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page == response.totalPages) null else page + 1
                )
            } else {
                LoadResult.Error(Throwable(NOTHING_FOUND))
            }
        } catch (t: Throwable) {
            Log.e("MoviePagingSource", "load: ", t)
            return LoadResult.Error(t)
        }
    }

    companion object {
        const val NOTHING_FOUND = "NOTHING_FOUND"
    }
}