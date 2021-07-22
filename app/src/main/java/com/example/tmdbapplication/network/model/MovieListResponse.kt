package com.example.tmdbapplication.network.model

import com.example.tmdbapplication.database.entity.MovieEntity
import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<MovieResponse>,
    @SerializedName("total_pages") val totalPages: Int
)

object ResponseMapper {
    fun mapToMovieEntity(response: MovieListResponse): List<MovieEntity> {
        return with(response) {
            movies.map {
                MovieEntity(
                    movieId = it.id,
                    title = it.title,
                    overview = it.overview,
                    posterPath = it.posterPath,
                    backdropPath = it.backdropPath,
                    rating = it.rating,
                    releaseDate = it.releaseDate,
                    page = response.page
                )
            }
        }
    }
}


