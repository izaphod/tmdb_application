package com.example.tmdbapplication.domain.usecase

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class SearchMovieUseCase {

    fun execute(movieDataSource: MovieDataSource, query: String): Observable<PagingData<Movie>> {
        return movieDataSource
            .searchMovie(query)
            .subscribeOn(Schedulers.io())
    }
}