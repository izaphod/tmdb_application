package com.example.tmdbapplication.domain.usecase

import android.util.Log
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class GetMoviesFromWatchlistUseCase @Inject constructor(
    private val watchlistDataSource: WatchlistDataSource
) {

    fun execute(): Flow<List<Long>> {
        return watchlistDataSource.getWatchlist()
            .onStart { Log.d(TAG, "getWatchlist.onStart: ") }
            .onCompletion { Log.d(TAG, "getWatchlist.onCompletion: ") }
            .onEach { Log.d(TAG, "getWatchlist.onEach: movie id list = $it") }
    }

    companion object {
        private const val TAG = "WatchlistMovies"
    }
}
