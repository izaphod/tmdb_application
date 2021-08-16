package com.example.tmdbapplication.presentation.pagedmovie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.tmdbapplication.data.paging.MovieRequestType
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.InsertToWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.asMovieViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PagedMovieViewModel @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val isInWatchlistUseCase: IsInWatchlistUseCase,
    private val insertToWatchlistUseCase: InsertToWatchlistUseCase,
    private val deleteFromWatchlistUseCase: DeleteFromWatchlistUseCase
) : ViewModel() {

    private val _movies = MutableLiveData<PagingData<MovieViewModel>>()
    val movies: LiveData<PagingData<MovieViewModel>> get() = _movies

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

    fun manageSelectedInWatchlist(movie: MovieViewModel) {
        viewModelScope.launch {
            if (movie.isInWatchlist) {
                deleteFromWatchlist(movie.movie.movieId)
            } else {
                insertToWatchlist(movie.movie.movieId)
            }
        }
    }

    fun loadMovies(requestType: MovieRequestType) {
        coroutineScope.launch {
            when (requestType) {
                MovieRequestType.POPULAR -> {
                    movieDataSource.getPagedPopular()
                        .cachedIn(coroutineScope)
                        .map { pagingData ->
                            pagingData.map { movie -> movie.asMovieViewModel(isInWatchlistUseCase) }
                        }
                        .collectLatest { pagingData ->
                            _movies.value = pagingData
                        }
                }
                MovieRequestType.NOW_PLAYING -> {
                    movieDataSource.getPagedNowPlaying()
                        .cachedIn(coroutineScope)
                        .map { pagingData ->
                            pagingData.map { movie -> movie.asMovieViewModel(isInWatchlistUseCase) }
                        }
                        .collectLatest { pagingData ->
                            _movies.value = pagingData
                        }
                }
                MovieRequestType.UPCOMING -> {
                    movieDataSource.getPagedUpcoming()
                        .cachedIn(coroutineScope)
                        .map { pagingData ->
                            pagingData.map { movie -> movie.asMovieViewModel(isInWatchlistUseCase) }
                        }
                        .collectLatest { pagingData ->
                            _movies.value = pagingData
                        }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
