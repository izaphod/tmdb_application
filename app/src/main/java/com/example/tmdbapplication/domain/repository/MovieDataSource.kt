package com.example.tmdbapplication.domain.repository

import androidx.paging.PagingData
import com.example.tmdbapplication.data.network.model.MovieResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface MovieDataSource {

    fun getMoviesByPage(): Observable<PagingData<MovieResponse>>

    fun getMovieById(movieId: Long): Single<MovieResponse>
}