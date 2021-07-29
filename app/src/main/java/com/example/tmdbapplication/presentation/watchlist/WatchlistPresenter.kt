package com.example.tmdbapplication.presentation.watchlist

import android.util.Log
import com.example.tmdbapplication.data.network.model.asDomainModels
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.GetMoviesByIdUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.asMovieViewModels
import com.example.tmdbapplication.util.addTo
import com.example.tmdbapplication.util.disposeIfNeeded
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import javax.inject.Inject

class WatchlistPresenter @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val watchlistDataSource: WatchlistDataSource
) : MvpPresenter<WatchlistView>() {

    private var deleteDisposable: Disposable? = null

    private val compositeDisposable = CompositeDisposable()
    private val getMoviesByIdUseCase = GetMoviesByIdUseCase()
    private val deleteFromWatchlistUseCase = DeleteFromWatchlistUseCase()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setProgressBarVisibility(true)
        getMoviesByIdUseCase.execute(watchlistDataSource, movieDataSource)
            .map { movieResponses -> movieResponses.asDomainModels() }
            .map { movies ->
                movies.asMovieViewModels().onEach { movieViewModel ->
                    movieViewModel.isInWatchlist = true
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { movieViewModels ->
                    viewState.setProgressBarVisibility(false)
                    viewState.onNewMovie(movieViewModels)
                },
                { t ->
                    viewState.setProgressBarVisibility(false)
                    Log.e(TAG, "onFirstViewAttach.getMovieByIdUseCase:", t)
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

    fun onItemWatchlistPressed(movieViewModel: MovieViewModel) {
        deleteDisposable?.disposeIfNeeded(compositeDisposable)
        deleteDisposable = deleteFromWatchlistUseCase
            .execute(watchlistDataSource, movieViewModel.movie.movieId)
            .subscribe(
                {
                    Log.d(
                        TAG, "onItemWatchlistPressed.deleteFromWatchlistUseCase: " +
                                "${movieViewModel.movie.title} deleted"
                    )
                },
                { Log.e(TAG, "onItemWatchlistPressed.deleteFromWatchlistUseCase:", it) }
            )
            .addTo(compositeDisposable)
    }

    companion object {
        private const val TAG = "WatchlistPresenter"
    }
}