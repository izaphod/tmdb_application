package com.example.tmdbapplication.presentation.movielists

import android.util.Log
import com.example.tmdbapplication.database.entity.asUiModel
import com.example.tmdbapplication.presentation.model.MovieUiModel
import com.example.tmdbapplication.presentation.model.UiModelMapper
import com.example.tmdbapplication.repository.Repository
import com.example.tmdbapplication.util.addTo
import com.example.tmdbapplication.util.disposeIfNeeded
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import javax.inject.Inject

class MovieListsPresenter @Inject constructor(
    private val movieRepository: Repository,
) : MvpPresenter<MovieListsView>() {

    private var moviesDisposable: Disposable? = null
    private var watchlistDisposable: Disposable? = null

    private val compositeDisposable = CompositeDisposable()

    private var _lastLoadedPage = 1
    val lastLoadedPage: Int
        get() = _lastLoadedPage

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setProgressBarVisibility(true)
        movieRepository.onFirstAppOpened()
            .observeOn(AndroidSchedulers.mainThread())
            .map { movieEntities -> movieEntities.asUiModel() }
            .map { movieUiModels ->
                movieUiModels.onEach { movieUiModel ->
                    movieRepository.isInWatchlist(movieUiModel.movie.movieId)
                        .doOnSuccess { isInWatchlist ->
                            movieUiModel.isInWatchlist = isInWatchlist
                        }
                        .subscribe()
                        .addTo(compositeDisposable)
                }
            }
            .subscribe(
                { movieUiModels ->
                    viewState.setProgressBarVisibility(false)
                    viewState.onFirstOpen(movieUiModels)
                },
                {
                    viewState.setProgressBarVisibility(false)
                    Log.d(TAG, "onFirstViewAttach: ${it.message}")
                }
            )
            .addTo(compositeDisposable)
        Log.d(TAG, "onFirstViewAttach")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        Log.d(TAG, "onDestroy")
    }

    fun onMovieListScrolled(page: Int) {
        moviesDisposable?.disposeIfNeeded(compositeDisposable)
        watchlistDisposable?.disposeIfNeeded(compositeDisposable)
        moviesDisposable = movieRepository
            .getMovies(page)
            .observeOn(AndroidSchedulers.mainThread())
            .map { movieEntities ->
                _lastLoadedPage = movieEntities.last().page
                movieEntities.asUiModel()
            }
            .map { movieUiModels ->
                movieUiModels.onEach { movieUiModel ->
                    watchlistDisposable = movieRepository
                        .isInWatchlist(movieUiModel.movie.movieId)
                        .doOnSuccess { isInWatchlist ->
                            movieUiModel.isInWatchlist = isInWatchlist
                        }
                        .subscribe()
                        .addTo(compositeDisposable)
                }
            }
            .subscribe(
                { movieUiModels ->
                    viewState.onNewMovies(movieUiModels)
                    Log.d(TAG, "onMovieListScrolled: last loaded page = $_lastLoadedPage")
                },
                { Log.d(TAG, "onMovieListScrolled: ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    fun onWatchlistMenuPressed(movieUiModel: MovieUiModel, position: Int) {
        movieRepository
            .onWatchlistPressed(
                UiModelMapper.mapToWatchlistEntity(movieUiModel),
                movieUiModel.isInWatchlist
            )
        viewState.notifyWatchlistFlagChanged(position)

        if (!movieUiModel.isInWatchlist) {
            Log.d(TAG, "onWatchlistPressed: ${movieUiModel.movie.title} added to watchlist")
        } else {
            Log.d(TAG, "onWatchlistPressed: ${movieUiModel.movie.title} deleted from watchlist")
        }
    }

    companion object {
        private const val TAG = "MovieListPresenter"
    }
}