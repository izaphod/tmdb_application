package com.example.tmdbapplication.data.network.model

import com.example.tmdbapplication.domain.model.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("release_date") val releaseDate: String? = null,
)

fun List<MovieResponse>.asDomainModel(): List<Movie> {
    return map { movieResponse ->
        movieResponse.asDomainModel()
    }
}

fun MovieResponse.asDomainModel(): Movie {
    return Movie(
        movieId = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        rating = rating,
        releaseDate = releaseDate
    )
}