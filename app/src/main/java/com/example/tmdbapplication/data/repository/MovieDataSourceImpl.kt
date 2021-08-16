package com.example.tmdbapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.tmdbapplication.data.network.MovieApiService
import com.example.tmdbapplication.data.network.model.asDomainModel
import com.example.tmdbapplication.data.paging.MoviePagingSource
import com.example.tmdbapplication.data.paging.MovieRequestType
import com.example.tmdbapplication.data.paging.SearchPagingSource
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.util.formatted
import com.example.tmdbapplication.util.rollDownTwoWeeks
import com.example.tmdbapplication.util.rollUpFourWeeks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDataSourceImpl @Inject constructor(
    private val movieApiService: MovieApiService
) : MovieDataSource {

    private val pagingConfig = PagingConfig(
        pageSize = 20,
        enablePlaceholders = true,
        maxSize = 30,
        prefetchDistance = 5,
        initialLoadSize = 40
    )

    override suspend fun getMovies(): Flow<Triple<List<Movie>, List<Movie>, List<Movie>>> {
        val today = Calendar.getInstance().time
        return combine(
            flowOf(movieApiService.getPopularMovies(page = 1).movies.asDomainModel()),
            flowOf(
                movieApiService.discoverMovies(
                    dateFrom = today.rollDownTwoWeeks().formatted(),
                    dateTo = today.formatted(),
                    releaseType = PREMIER_AND_THEATRICAL,
                    sortBy = POPULARITY_DESC,
                    page = 1
                ).movies.asDomainModel()
            ),
            flowOf(
                movieApiService.discoverMovies(
                    dateFrom = today.formatted(),
                    dateTo = today.rollUpFourWeeks().formatted(),
                    releaseType = PREMIER_AND_THEATRICAL,
                    sortBy = POPULARITY_DESC,
                    page = 1
                ).movies.asDomainModel()
            )
        ) { popular, nowPlaying, upcoming -> Triple(popular, nowPlaying, upcoming) }
    }

    override fun getPagedPopular(): Flow<PagingData<Movie>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { MoviePagingSource(movieApiService, MovieRequestType.POPULAR) }
        ).flow
    }

    override fun getPagedNowPlaying(): Flow<PagingData<Movie>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { MoviePagingSource(movieApiService, MovieRequestType.NOW_PLAYING) }
        ).flow
    }

    override fun getPagedUpcoming(): Flow<PagingData<Movie>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { MoviePagingSource(movieApiService, MovieRequestType.UPCOMING) }
        ).flow
    }

    override fun searchMovie(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { SearchPagingSource(movieApiService, query) }
        ).flow
    }

    override suspend fun getMovieById(movieId: Long): Flow<Movie> {
        return flowOf(movieApiService.getMovieById(movieId).asDomainModel())
    }

    companion object {
        const val PREMIER_AND_THEATRICAL = "1|3"
        const val POPULARITY_DESC = "popularity.desc"
    }
}