package com.example.tmdbapplication.domain.usecase

import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import com.example.tmdbapplication.domain.repository.WatchlistDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class GetMoviesByIdUseCase {

    fun execute(
        watchlistDataSource: WatchlistDataSource,
        movieDataSource: MovieDataSource
    ): Single<List<Movie>> {
        return watchlistDataSource
            .getWatchlist()
            .switchMap<Long> { movieIdList -> Observable.fromIterable(movieIdList) }
            .concatMap<Movie> { movieId -> movieDataSource.getMovieById(movieId).toObservable() }
            .toList()
            .subscribeOn(Schedulers.io())
    }
}
