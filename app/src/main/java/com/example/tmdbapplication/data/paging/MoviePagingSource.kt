package com.example.tmdbapplication.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.MovieListResponse
import com.example.tmdbapplication.data.network.model.asDomainModel
import com.example.tmdbapplication.data.repository.MovieDataSourceImpl
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.util.formatted
import com.example.tmdbapplication.util.rollDownTwoWeeks
import com.example.tmdbapplication.util.rollUpFourWeeks
import java.util.*
import javax.inject.Inject

class MoviePagingSource @Inject constructor(
    private val movieApiService: MovieApiService,
    private val requestType: MovieRequestType
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = response(requestType, page)
            LoadResult.Page(
                data = response.movies.asDomainModel(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page == response.totalPages) null else page + 1
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    private suspend fun response(requestType: MovieRequestType, page: Int): MovieListResponse {
        val today = Calendar.getInstance().time
        return when (requestType) {
            MovieRequestType.POPULAR -> movieApiService
                .getPopularMovies(page = page)
            MovieRequestType.NOW_PLAYING -> movieApiService
                .discoverMovies(
                    dateFrom = today.rollDownTwoWeeks().formatted(),
                    dateTo = today.formatted(),
                    releaseType = MovieDataSourceImpl.PREMIER_AND_THEATRICAL,
                    sortBy = MovieDataSourceImpl.POPULARITY_DESC,
                    page = page
                )
            MovieRequestType.UPCOMING -> movieApiService
                .discoverMovies(
                    dateFrom = today.formatted(),
                    dateTo = today.rollUpFourWeeks().formatted(),
                    releaseType = MovieDataSourceImpl.PREMIER_AND_THEATRICAL,
                    sortBy = MovieDataSourceImpl.POPULARITY_DESC,
                    page = page
                )
        }
    }
}