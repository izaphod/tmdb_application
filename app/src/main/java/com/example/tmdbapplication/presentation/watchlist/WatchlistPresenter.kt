package com.example.tmdbapplication.presentation.watchlist

import android.util.Log
import com.example.tmdbapplication.network.model.asUiModel
import com.example.tmdbapplication.repository.Repository
import com.example.tmdbapplication.util.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import javax.inject.Inject

class WatchlistPresenter @Inject constructor(
    private val movieRepository: Repository
) : MvpPresenter<WatchlistView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setProgressBarVisibility(true)
        movieRepository.onWatchlistOpened()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { movieResponseList -> movieResponseList.asUiModel() }
            .map {
                it.onEach { movieUiModel ->
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
                    Log.d(
                        TAG,
                        "onFirstViewAttach: ${
                            movieUiModels.joinToString(
                                "\n",
                                "\n"
                            ) { it.movie.title }
                        }"
                    )
                    if (movieUiModels.isNotEmpty()) {
                        viewState.setProgressBarVisibility(false)
                        viewState.showWatchlist(movieUiModels)
                    } else {
                        viewState.setProgressBarVisibility(false)
                        viewState.setEmptyScreenVisibility(true)
                    }
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

    companion object {
        private const val TAG = "WatchlistPresenter"
    }
}