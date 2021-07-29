package com.example.tmdbapplication.domain.repository

import androidx.paging.PagingData
import com.example.tmdbapplication.data.network.model.MovieResponse
import io.reactivex.rxjava3.core.Observable

interface MovieDataSource {

    fun fetchMovies(): Observable<PagingData<MovieResponse>>
}