package com.example.tmdbapplication.data.network

import com.example.tmdbapplication.data.network.model.MovieListResponse
import com.example.tmdbapplication.data.network.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieById(
        @Path("movie_id") movieId: Long
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("primary_release_date.gte") dateFrom: String,
        @Query("primary_release_date.lte") dateTo: String,
        @Query("with_release_type") releaseType: String,
        @Query("sort_by") sortBy: String,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieListResponse
}