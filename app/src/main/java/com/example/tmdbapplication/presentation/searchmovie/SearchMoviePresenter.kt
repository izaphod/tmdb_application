package com.example.tmdbapplication.presentation.searchmovie

import android.util.Log
import androidx.paging.map
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import com.example.tmdbapplication.domain.usecase.DeleteFromWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.InsertToWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import com.example.tmdbapplication.domain.usecase.SearchMovieUseCase
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.model.asMovieViewModels
import com.example.tmdbapplication.util.addTo
import com.example.tmdbapplication.util.disposeIfNeeded
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import javax.inject.Inject

class SearchMoviePresenter @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val watchlistDataSource: WatchlistDataSource
) : MvpPresenter<SearchMovieView>() {

    private var searchDisposable: Disposable? = null
    private var watchlistDisposable: Disposable? = null
    private var insertDisposable: Disposable? = null
    private var deleteDisposable: Disposable? = null

    private val compositeDisposable = CompositeDisposable()
    private val searchMovieUseCase = SearchMovieUseCase()
    private val isInWatchlistUseCase = IsInWatchlistUseCase()
    private val insertToWatchlistUseCase = InsertToWatchlistUseCase()
    private val deleteFromWatchlistUseCase = DeleteFromWatchlistUseCase()

    private var currentQuery = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setEmptyScreenVisibility(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    // TODO: 8/3/21 Exception when view destroyed:
    //  java.lang.IllegalStateException: Attempt to collect twice from pageEventFlow,
    //  which is an illegal operation.
    //  Did you forget to call Flow<PagingData<*>>.cachedIn(coroutineScope)?
    fun onSearchPressed(query: String) {
        searchDisposable?.disposeIfNeeded(compositeDisposable)
        watchlistDisposable?.disposeIfNeeded(compositeDisposable)
        query.let {
            when {
                it != currentQuery -> {
                    searchDisposable = searchMovieUseCase
                        .execute(movieDataSource, query)
                        .map { pagingDataDomain ->
                            pagingDataDomain.map { movie ->
                                movie.asMovieViewModels().also { movieViewModel ->
                                    watchlistDisposable = isInWatchlistUseCase
                                        .execute(watchlistDataSource, movieViewModel.movie.movieId)
                                        .subscribe(
                                            { isInWatchlist ->
                                                movieViewModel.isInWatchlist = isInWatchlist
                                            },
                                            { t ->
                                                Log.e(
                                                    TAG,
                                                    "onSearchPressed.isInWatchlistUseCase:",
                                                    t
                                                )
                                            }
                                        )
                                        .addTo(compositeDisposable)
                                }
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { pagingDataViewModel ->
                                viewState.showSearchResult(pagingDataViewModel)
                                viewState.setEmptyScreenVisibility(false)
                            },
                            { t -> Log.e(TAG, "onSearchPressed.searchMovieUseCase:", t) }
                        )
                        .addTo(compositeDisposable)
                }
                it.isBlank() -> { viewState.showEmptyQueryToast() }
                else -> { return }
            }
        }
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
                .execute(watchlistDataSource, movieViewModel.movie.movieId)
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
        private const val TAG = "SearchMoviePresenter"
    }
}