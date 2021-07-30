package com.example.tmdbapplication.domain.usecase

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class GetMoviesByPageUseCase {

    fun execute(movieDataSource: MovieDataSource): Observable<PagingData<Movie>> {
        return movieDataSource
            .getMoviesByPage()
            .subscribeOn(Schedulers.io())
    }
}