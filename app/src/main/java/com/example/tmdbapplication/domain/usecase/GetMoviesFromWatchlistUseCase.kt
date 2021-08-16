package com.example.tmdbapplication.domain.usecase

import android.util.Log
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class GetMoviesFromWatchlistUseCase @Inject constructor(
    private val watchlistDataSource: WatchlistDataSource,
    private val movieDataSource: MovieDataSource
) {

    suspend fun execute(): List<Movie> {
        return flowOf(watchlistDataSource.getWatchlist())
            .onStart { Log.d(TAG, "getWatchlist.onStart: ") }
            .onCompletion { Log.d(TAG, "getWatchlist.onCompletion: ") }
            .onEach { Log.d(TAG, "getWatchlist.onEach: movie id list = $it") }
            .flatMapLatest { movieIdList: List<Long> -> movieIdList.asFlow() }
            .onStart { Log.d(TAG, "flatMapLatest.onStart: ") }
            .onCompletion { Log.d(TAG, "flatMapLatest.onCompletion: ") }
            .onEach { Log.d(TAG, "flatMapLatest.onEach: movie id = $it") }
            .flatMapConcat { movieId -> movieDataSource.getMovieById(movieId) }
            .onStart { Log.d(TAG, "flatMapConcat.onStart: ") }
            .onCompletion { Log.d(TAG, "flatMapConcat.onCompletion: ") }
            .onEach { Log.d(TAG, "flatMapConcat.onEach: movie = ${it.title}") }
            .toList()
    }

    companion object {
        private const val TAG = "WatchlistMovies"
    }
}
