package com.example.tmdbapplication.presentation.watchlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.GetMovieByIdUseCase
import com.example.tmdbapplication.domain.usecase.GetMoviesFromWatchlistUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.Status
import com.example.tmdbapplication.presentation.model.asMovieViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val getMoviesFromWatchlistUseCase: GetMoviesFromWatchlistUseCase,
    private val deleteFromWatchlistUseCase: DeleteFromWatchlistUseCase,
    private val getMovieByIdUseCase: GetMovieByIdUseCase
) : ViewModel() {

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> get() = _status

    private val _movies = MutableLiveData<List<MovieViewModel>>()
    val movies: LiveData<List<MovieViewModel>> get() = _movies

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        loadMoviesFromWatchlist()
    }

    fun manageSelectedInWatchlist(movie: MovieViewModel) {
        viewModelScope.launch {
            try {
                deleteFromWatchlist(movie.movie.movieId)
            } catch (t: Throwable) {
                Log.e(TAG, "manageSelectedInWatchlist: ", t)
            }
        }
    }

    private fun loadMoviesFromWatchlist() {
        coroutineScope.launch {
            try {
                _status.value = Status.LOADING
                getMoviesFromWatchlistUseCase
                    .execute()
                    .distinctUntilChanged()
                    .collectLatest { movieIdList ->
                        val listResult = movieIdList
                            .map { movieId -> getMovieByIdUseCase.execute(movieId) }
                            .toList()
                            .map { movie ->
                                movie.asMovieViewModel()
                                    .also { movieViewModel -> movieViewModel.isInWatchlist = true }
                            }
                        if (listResult.isNotEmpty()) {
                            _status.value = Status.SUCCESS
                            _movies.value = listResult
                        } else {
                            _status.value = Status.EMPTY
                            _movies.value = emptyList()
                        }
                    }
            } catch (t: Throwable) {
                _status.value = Status.ERROR
                _movies.value = emptyList()
            }
        }
    }

    private suspend fun deleteFromWatchlist(movieId: Long) {
        withContext(Dispatchers.IO) {
            deleteFromWatchlistUseCase.execute(movieId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object {
        private const val TAG = "WatchlistViewModel"
    }
}