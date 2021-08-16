package com.example.tmdbapplication.presentation.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.InsertToWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.asMovieViewModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class Status {
    LOADING,
    ERROR,
    DONE,
    EMPTY
}

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val watchlistDataSource: WatchlistDataSource
) : ViewModel() {

    private val isInWatchlistUseCase = IsInWatchlistUseCase()
    private val insertToWatchlistUseCase = InsertToWatchlistUseCase()
    private val deleteFromWatchlistUseCase = DeleteFromWatchlistUseCase()

    private var _movies =
        MutableLiveData<Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>>>()
    val movies: LiveData<Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>>>
        get() = _movies

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        loadMovies()
    }

    fun manageSelectedInWatchlist(movie: MovieViewModel) {
        viewModelScope.launch {
            if (movie.isInWatchlist) {
                deleteFromWatchlist(movie.movie.movieId)
            } else {
                insertToWatchlist(movie.movie.movieId)
            }
        }
    }

    private fun loadMovies() {
        coroutineScope.launch {
            try {
                _status.value = Status.LOADING
                movieDataSource.getMovies()
                    .map { tripleMovies ->
                        tripleMovies.asMovieViewModels()
                            .also { tripleMovieViewModels ->
                            checkInWatchlist(tripleMovieViewModels)
                        }
                    }
                    .collectLatest { resultTripleList ->
                        if (resultTripleList.first.isNotEmpty() &&
                            resultTripleList.second.isNotEmpty() &&
                            resultTripleList.third.isNotEmpty()
                        ) {
                            _status.value = Status.DONE
                            _movies.value = resultTripleList
                        } else {
                            _status.value = Status.EMPTY
                            _movies.value = Triple(emptyList(), emptyList(), emptyList())
                        }
                    }
            } catch (t: Throwable) {
                _status.value = Status.ERROR
                _movies.value = Triple(emptyList(), emptyList(), emptyList())
            }
        }
    }

    private suspend fun insertToWatchlist(movieId: Long) {
        withContext(Dispatchers.IO) {
            insertToWatchlistUseCase.execute(watchlistDataSource, movieId)
        }
    }

    private suspend fun deleteFromWatchlist(movieId: Long) {
        withContext(Dispatchers.IO) {
            deleteFromWatchlistUseCase.execute(watchlistDataSource, movieId)
        }
    }

    private suspend fun checkInWatchlist(
        tripleList: Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>>
    ): Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>> {
        return tripleList.apply {
            withContext(Dispatchers.IO) {
                first.forEach { movieViewModel ->
                    movieViewModel.isInWatchlist = isInWatchlistUseCase
                        .execute(watchlistDataSource, movieViewModel.movie.movieId)
                }
                second.forEach { movieViewModel ->
                    movieViewModel.isInWatchlist = isInWatchlistUseCase
                        .execute(watchlistDataSource, movieViewModel.movie.movieId)
                }
                third.forEach { movieViewModel ->
                    movieViewModel.isInWatchlist = isInWatchlistUseCase
                        .execute(watchlistDataSource, movieViewModel.movie.movieId)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object {
        private const val TAG = "MovieListViewModel"
    }
}