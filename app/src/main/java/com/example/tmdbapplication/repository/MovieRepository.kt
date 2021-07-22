package com.example.tmdbapplication.repository

import android.util.Log
import com.example.tmdbapplication.database.MovieDatabase
import com.example.tmdbapplication.database.entity.MovieEntity
import com.example.tmdbapplication.database.entity.WatchlistEntity
import com.example.tmdbapplication.network.MovieApiService
import com.example.tmdbapplication.network.model.MovieResponse
import com.example.tmdbapplication.network.model.ResponseMapper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val movieDatabase: MovieDatabase
) : Repository {

    override fun onFirstAppOpened(): Observable<List<MovieEntity>> {
        return Observable
            .concat(getMoviesByPage(page = 1).toObservable(), dbMovies(page = 1))
            .subscribeOn(Schedulers.io())
    }

    // TODO: 7/22/21 Исправить ошибку при пэйджинации с пустым листом
    override fun getMovies(page: Int): Observable<List<MovieEntity>> {
        return Observable
            .concat(getMoviesByPage(page).toObservable(), dbMovies(page))
            .subscribeOn(Schedulers.io())
    }

    override fun onWatchlistPressed(watchlistEntity: WatchlistEntity, isInWatchlist: Boolean) {
        if (!isInWatchlist) {
            addToWatchlist(watchlistEntity)
                .subscribe(
                    { Log.d(TAG, "onWatchlistPressed.addToWatchlist: ${watchlistEntity.movieId}") },
                    { Log.d(TAG, "onWatchlistPressed.addToWatchlist: ${it.message}") }
                )
        } else {
            deleteFromWatchlist(watchlistEntity.movieId)
                .subscribe(
                    { Log.d(TAG, "onWatchlistPressed..deleteFromWatchlist: ${watchlistEntity.movieId}") },
                    { Log.d(TAG, "onWatchlistPressed.deleteFromWatchlist: ${it.message}") }
                )
        }
    }

    // TODO: 7/22/21 Не возвращает список, оператор toList() не срабатывает
    override fun onWatchlistOpened(): Single<List<MovieResponse>> {
        return wlMovies()
            .doOnComplete {
                Log.d(TAG, "wlMovies.doOnComplete")
            }
            .doOnSubscribe {
                Log.d(TAG, "wlMovies.doOnSubscribe")
            }
            .doOnNext {
                Log.d(TAG, "wlMovies.doOnNext: ${it.joinToString()}")
            }
            .switchMap {
                Observable.fromIterable(it)
            }
            .doOnComplete {
                Log.d(TAG, "switchMap.doOnComplete")
            }
            .doOnSubscribe {
                Log.d(TAG, "switchMap.doOnSubscribe")
            }
            .doOnNext {
                Log.d(TAG, "switchMap.doOnNext: ${it.movieId}")
            }
            .concatMap<MovieResponse> {
                getMovieById(it.movieId).toObservable()
            }
            .doOnComplete {
                Log.d(TAG, "concatMap.doOnComplete")
            }
            .doOnSubscribe {
                Log.d(TAG, "concatMap.doOnSubscribe")
            }
            .doOnNext {
                Log.d(TAG, "concatMap.doOnNext: ${it.title}")
            }
            .toList()
            .doOnSubscribe {
                Log.d(TAG, "toList.doOnSubscribe")
            }
            .doOnSuccess {
                Log.d(TAG, "toList.doOnNext: ${it.joinToString()}")
            }
    }

    override fun isInWatchlist(movieId: Long): Single<Boolean> {
        return movieDatabase.watchlistDao()
            .isInWatchlist(movieId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "isInWatchlist: movie with id = $movieId is in watchlist = $it")
            }
    }

    private fun getMovieById(movieId: Long): Single<MovieResponse> {
        return movieApiService.getMovieById(movieId)
            .subscribeOn(Schedulers.io())
    }

    private fun dbMovies(page: Int): Observable<List<MovieEntity>> {
        return movieDatabase.movieDao().getMovies(page)
            .subscribeOn(Schedulers.io())
            .doOnNext { movies ->
                Log.d(
                    TAG,
                    "dbMovies: movies = ${
                        movies.joinToString("\n", "\n") { it.title }
                    }"
                )
            }
    }

    private fun getMoviesByPage(page: Int): Single<List<MovieEntity>> {
        return movieApiService.getMovies(page)
            .subscribeOn(Schedulers.io())
            .map { response ->
                ResponseMapper.mapToMovieEntity(response)
            }
            .doOnSuccess { movies ->
                if (page == 1) {
                    clearMovies()
                        .andThen(insertMovies(movies))
                        .subscribe()
                } else {
                    insertMovies(movies)
                        .subscribe()
                }
                Log.d(
                    TAG,
                    "getMoviesByPage(page = $page): movies = ${
                        movies.joinToString("\n", "\n") { it.title }
                    }"
                )
            }
    }

    private fun clearMovies(): Completable {
        return movieDatabase.movieDao().clearMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Log.d(TAG, "clearMovies: movie_database cleared")
            }
    }

    private fun insertMovies(movieEntities: List<MovieEntity>): Completable {
        return movieDatabase.movieDao()
            .insertMovies(movieEntities)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Log.d(
                    TAG,
                    "insertMovies: ${movieEntities.size} movies inserted"
                )
            }
    }

    private fun wlMovies(): Observable<List<WatchlistEntity>> {
        return movieDatabase.watchlistDao().getWatchlist()
            .subscribeOn(Schedulers.io())
            .doOnNext { watchlist ->
                Log.d(
                    TAG,
                    "wlMovies: watchlist movies = ${
                        watchlist.joinToString(
                            "\n",
                            "\n"
                        ) { it.movieId.toString() }
                    }"
                )
            }
            .doOnError {
                Log.d(TAG, "wlMovies: ${it.message}")
            }
    }

    private fun addToWatchlist(watchlistEntity: WatchlistEntity): Completable {
        return movieDatabase.watchlistDao()
            .insertMovie(watchlistEntity)
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                Log.d(
                    TAG,
                    "addToWatchlist: movie with id = ${watchlistEntity.movieId} inserted"
                )
            }
    }

    private fun deleteFromWatchlist(movieId: Long): Completable {
        return movieDatabase.watchlistDao().deleteMovie(movieId)
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                Log.d(TAG, "deleteFromWatchlist: movie with id = $movieId deleted")
            }
    }

    companion object {
        private const val TAG = "MovieRepository"
    }
}