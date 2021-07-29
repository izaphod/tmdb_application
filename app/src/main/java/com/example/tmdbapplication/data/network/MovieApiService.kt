package com.example.tmdbapplication.data.network

import com.example.tmdbapplication.data.network.model.MovieListResponse
import com.example.tmdbapplication.data.network.model.MovieResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/popular")
    fun getMovies(
        @Query("page") page: Int
    ): Single<MovieListResponse>

    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int
    ): Single<MovieListResponse>

    @GET("movie/{movie_id}")
    fun getMovieById(
        @Path("movie_id") movieId: Long
    ): Single<MovieResponse>
}