package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.data.network.model.MovieResponse
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class GetMoviesByIdUseCase {

    // TODO: 7/30/21 concatMap{} never ends, toList() doesn't work
    fun execute(
        watchlistDataSource: WatchlistDataSource,
        movieDataSource: MovieDataSource
    ): Single<List<MovieResponse>> {
        return watchlistDataSource.getWatchlist()
            .switchMap { Observable.fromIterable(it) }
            .concatMap { movieDataSource.getMovieById(it.movieId).toObservable() }
            .toList()
            .subscribeOn(Schedulers.io())
    }
}
