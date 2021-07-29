package com.example.tmdbapplication.presentation.movielist

import android.util.Log
import androidx.paging.map
import com.example.tmdbapplication.data.network.model.asDomainModel
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.GetMoviesUseCase
import com.example.tmdbapplication.domain.usecase.InsertToWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import com.example.tmdbapplication.presentation.model.asWatchlistEntity
import com.example.tmdbapplication.presentation.model.asMovieViewModel
import com.example.tmdbapplication.util.addTo
import com.example.tmdbapplication.util.disposeIfNeeded
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import javax.inject.Inject

class MovieListPresenter @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val watchlistDataSource: WatchlistDataSource
) : MvpPresenter<MovieListView>() {

    private var insertDisposable: Disposable? = null
    private var deleteDisposable: Disposable? = null

    private val compositeDisposable = CompositeDisposable()
    private val getMoviesUseCase = GetMoviesUseCase()
    private val isInWatchlistUseCase = IsInWatchlistUseCase()
    private val insertToWatchlistUseCase = InsertToWatchlistUseCase()
    private val deleteFromWatchlistUseCase = DeleteFromWatchlistUseCase()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setProgressBarVisibility(true)
        getMoviesUseCase.execute(movieDataSource)
            .map { pagingData ->
                pagingData.map { movieResponse ->
                    movieResponse.asDomainModel()
                }
            }
            .map { pagingDataResponse ->
                pagingDataResponse.map { movie ->
                    Log.d(TAG, "onFirstViewAttach.getMoviesUseCase: ${movie.title}")
                    movie.asMovieViewModel().also { movieViewModel ->
                        isInWatchlistUseCase
                            .execute(watchlistDataSource, movieViewModel.movie.movieId)
                            .subscribe(
                                { isInWatchlist -> movieViewModel.isInWatchlist = isInWatchlist },
                                { t -> Log.e(TAG, "onFirstViewAttach.isInWatchlistUseCase:", t) }
                            )
                            .addTo(compositeDisposable)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { pagingDataViewModel ->
                    viewState.setProgressBarVisibility(false)
                    viewState.onNewMovies(pagingDataViewModel)
                },
                { t ->
                    viewState.setProgressBarVisibility(false)
                    Log.e(TAG, "onFirstViewAttach.getMoviesUseCase:", t)
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
        if (movieViewModel.isInWatchlist) {
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
        } else {
            insertDisposable?.disposeIfNeeded(compositeDisposable)
            insertDisposable = insertToWatchlistUseCase
                .execute(watchlistDataSource, movieViewModel.asWatchlistEntity())
                .subscribe(
                    {
                        Log.d(
                            TAG, "onItemWatchlistPressed.insertToWatchlistUseCase: " +
                                    "${movieViewModel.movie.title} inserted"
                        )
                    },
                    { Log.e(TAG, "onItemWatchlistPressed.insertToWatchlistUseCase:", it) }
                )
                .addTo(compositeDisposable)
        }
    }

    companion object {
        private const val TAG = "MovieListsPresenter"
    }
}