package com.example.tmdbapplication.presentation.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.GetMoviesUseCase
import com.example.tmdbapplication.domain.usecase.InsertToWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.Status
import com.example.tmdbapplication.presentation.model.asMovieViewModels
import com.example.tmdbapplication.presentation.model.expandForViewPagerList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val isInWatchlistUseCase: IsInWatchlistUseCase,
    private val insertToWatchlistUseCase: InsertToWatchlistUseCase,
    private val deleteFromWatchlistUseCase: DeleteFromWatchlistUseCase
) : ViewModel() {

    private var _movies =
        MutableLiveData<Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>>>()
    val movies: LiveData<Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>>>
        get() = _movies

    private var _trendingMovies = MutableLiveData<List<MovieViewModel>>()
    val trendingMovies: LiveData<List<MovieViewModel>> get() = _trendingMovies

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        loadTrendingMovies()
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
                getMoviesUseCase.executeTriple()
                    .map { tripleMovies ->
                        tripleMovies.asMovieViewModels(isInWatchlistUseCase)
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

    private fun loadTrendingMovies() {
        coroutineScope.launch {
            getMoviesUseCase.executeTrending()
                .map { it.asMovieViewModels() }
                .map { it.expandForViewPagerList() }
                .collectLatest { _trendingMovies.value = it }
        }
    }

    private suspend fun insertToWatchlist(movieId: Long) {
        withContext(Dispatchers.IO) {
            insertToWatchlistUseCase.execute(movieId)
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
        private const val TAG = "MovieListViewModel"
    }
}