package com.example.tmdbapplication.presentation.movielists

import android.util.Log
import com.example.tmdbapplication.data.network.model.asDomainModel
import com.example.tmdbapplication.presentation.model.WatchlistViewModel
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.GetMoviesUseCase
import com.example.tmdbapplication.domain.usecase.InsertToWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.asWatchlistEntity
import com.example.tmdbapplication.presentation.model.asWatchlistViewModel
import com.example.tmdbapplication.util.addTo
import com.example.tmdbapplication.util.disposeIfNeeded
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import moxy.MvpPresenter
import javax.inject.Inject

class MovieListPresenter @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val watchlistDataSource: WatchlistDataSource
) : MvpPresenter<MovieListView>() {

    private var viewModelSubject: BehaviorSubject<MovieViewModel> = BehaviorSubject.create()
    private var movieViewModel = MovieViewModel(
        MovieViewModel.State.Loading,
        emptyList()
    )

    private var moviesDisposable: Disposable? = null
    private var watchlistDisposable: Disposable? = null
    private var insertDisposable: Disposable? = null
    private var deleteDisposable: Disposable? = null

    private val compositeDisposable = CompositeDisposable()
    private val getMoviesUseCase = GetMoviesUseCase()
    private val isInWatchlistUseCase = IsInWatchlistUseCase()
    private val insertToWatchlistUseCase = InsertToWatchlistUseCase()
    private val deleteFromWatchlistUseCase = DeleteFromWatchlistUseCase()

    private var _lastLoadedPage = 1
    val lastLoadedPage: Int
        get() = _lastLoadedPage

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMoviesUseCase.execute(movieDataSource, 1)
            .map { movieListResponse -> movieListResponse.movies.asDomainModel() }
            .map { movies ->
                movies.asWatchlistViewModel().onEach { watchlistViewModel ->
                    isInWatchlistUseCase.execute(
                        watchlistDataSource,
                        watchlistViewModel.movie.movieId
                    ).subscribe(
                        { isInWatchlist -> watchlistViewModel.isInWatchlist = isInWatchlist },
                        { th -> Log.e(TAG, "onFirstViewAttach.isInWatchlistUseCase:", th) }
                    ).addTo(compositeDisposable)
                }
            }
            .subscribe(
                { watchlistViewModels -> updateMovies(watchlistViewModels) },
                { th ->
                    updateModel {
                        copy(state = MovieViewModel.State.Error(th.message.toString()))
                    }
                }
            ).addTo(compositeDisposable)
        Log.d(TAG, "onFirstViewAttach")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        Log.d(TAG, "onDestroy")
    }

    fun observeViewModels(): Observable<MovieViewModel> = viewModelSubject

    fun onMovieListScrolled(page: Int) {
        moviesDisposable?.disposeIfNeeded(compositeDisposable)
        moviesDisposable = getMoviesUseCase.execute(movieDataSource, page)
            .observeOn(AndroidSchedulers.mainThread())
            .map { movieListResponse ->
                _lastLoadedPage = movieListResponse.page
                movieListResponse.movies.asDomainModel()
            }
            .map { movies ->
                movies.asWatchlistViewModel().onEach { watchlistViewModel ->
                    watchlistDisposable?.disposeIfNeeded(compositeDisposable)
                    watchlistDisposable = isInWatchlistUseCase.execute(
                        watchlistDataSource,
                        watchlistViewModel.movie.movieId
                    ).subscribe(
                        { isInWatchlist -> watchlistViewModel.isInWatchlist = isInWatchlist },
                        { th -> Log.e(TAG, "onFirstViewAttach.isInWatchlistUseCase:", th) }
                    ).addTo(compositeDisposable)
                }
            }
            .subscribe(
                { watchlistViewModels -> updateMovies(watchlistViewModels) },
                { th ->
                    updateModel {
                        copy(state = MovieViewModel.State.Error(th.message.toString()))
                    }
                }
            )
            .addTo(compositeDisposable)
    }

    fun onItemWatchlistPressed(watchlistViewModel: WatchlistViewModel, position: Int) {
        if (watchlistViewModel.isInWatchlist) {
            deleteDisposable?.disposeIfNeeded(compositeDisposable)
            deleteDisposable = deleteFromWatchlistUseCase
                .execute(watchlistDataSource, watchlistViewModel.movie.movieId)
                .subscribe { viewState.notifyWatchlistFlagChanged(position) }
                .addTo(compositeDisposable)
        } else {
            insertDisposable?.disposeIfNeeded(compositeDisposable)
            insertDisposable = insertToWatchlistUseCase
                .execute(watchlistDataSource, watchlistViewModel.asWatchlistEntity())
                .subscribe { viewState.notifyWatchlistFlagChanged(position) }
                .addTo(compositeDisposable)
        }
    }

    private fun updateMovies(movies: List<WatchlistViewModel>) {
        if (movies.isEmpty()) {
            updateModel {
                copy(
                    state = MovieViewModel.State.Empty,
                    movies = emptyList()
                )
            }
        } else {
            updateModel {
                copy(
                    state = MovieViewModel.State.Content,
                    movies = movies
                )
            }
        }
    }

    private fun updateModel(mapper: MovieViewModel.() -> MovieViewModel = { this }) {
        movieViewModel = movieViewModel.mapper()
        viewModelSubject.onNext(movieViewModel)
    }

    companion object {
        private const val TAG = "MovieListsPresenter"
    }
}