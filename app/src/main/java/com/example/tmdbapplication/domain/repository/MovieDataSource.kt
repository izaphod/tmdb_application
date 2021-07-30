package com.example.tmdbapplication.domain.repository

import androidx.paging.PagingData
import com.example.tmdbapplication.domain.model.Movie
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface MovieDataSource {

    fun getMoviesByPage(): Observable<PagingData<Movie>>

    fun getMovieById(movieId: Long): Single<Movie>
}