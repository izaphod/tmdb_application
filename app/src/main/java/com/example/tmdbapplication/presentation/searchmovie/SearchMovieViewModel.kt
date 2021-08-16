package com.example.tmdbapplication.presentation.searchmovie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import com.example.tmdbapplication.domain.usecase.*
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.asMovieViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchMovieViewModel @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val watchlistDataSource: WatchlistDataSource
) : ViewModel() {

    private val searchMovieUseCase = SearchMovieUseCase()
    private val isInWatchlistUseCase = IsInWatchlistUseCase()
    private val insertToWatchlistUseCase = InsertToWatchlistUseCase()
    private val deleteFromWatchlistUseCase = DeleteFromWatchlistUseCase()

    private val _movies = MutableLiveData<PagingData<MovieViewModel>>()
    val movies: LiveData<PagingData<MovieViewModel>> get() = _movies

    private val _showInitialMessage = MutableLiveData<Boolean>()
    val showInitialMessage: MutableLiveData<Boolean> get() = _showInitialMessage

    private val _showEmptyQuery = MutableLiveData<Boolean>()
    val showEmptyQuery: MutableLiveData<Boolean> get() = _showEmptyQuery

    private val _showEmptyResult = MutableLiveData<Boolean>()
    val showEmptyResult: MutableLiveData<Boolean> get() = _showEmptyQuery

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var latestQuery = ""

    init {
        _showInitialMessage.value = true
    }

    private fun searchMovie(query: String) {
        coroutineScope.launch {
            searchMovieUseCase.execute(movieDataSource, query)
                .cachedIn(coroutineScope)
                .map { pagingData ->
                    pagingData.map { movie ->
                        movie.asMovieViewModel().also { movieViewModel ->
                            movieViewModel.isInWatchlist = isInWatchlistUseCase
                                .execute(watchlistDataSource, movieViewModel.movie.movieId)
                        }
                    }
                }
                .collectLatest { pagingData ->
                    _movies.value = pagingData
                    _showInitialMessage.value = false
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

    fun manageSelectedInWatchlist(movie: MovieViewModel) {
        viewModelScope.launch {
            if (movie.isInWatchlist) {
                deleteFromWatchlist(movie.movie.movieId)
            } else {
                insertToWatchlist(movie.movie.movieId)
            }
        }
    }

    fun updateQuery(currentQuery: String) {
        when {
            currentQuery != latestQuery -> {
                if (currentQuery.isNotBlank()) { searchMovie(currentQuery) }
                latestQuery = currentQuery
            }
            currentQuery.isBlank() -> { _showEmptyQuery.value = true }
            else -> return
        }
    }

    fun showEmptyQueryComplete() {
        _showEmptyQuery.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
