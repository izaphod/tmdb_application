package com.example.tmdbapplication.domain.usecase

import androidx.paging.PagingData
import com.example.tmdbapplication.data.network.model.MovieResponse
import com.example.tmdbapplication.domain.repository.MovieDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class GetMoviesByPageUseCase {

    fun execute(movieDataSource: MovieDataSource): Observable<PagingData<MovieResponse>> =
        movieDataSource.getMoviesByPage()
            .subscribeOn(Schedulers.io())
}