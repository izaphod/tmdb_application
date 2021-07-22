package com.example.tmdbapplication.network.model

import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.presentation.model.MovieUiModel
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

fun List<MovieResponse>.asUiModel(): List<MovieUiModel> {
    return map { movieResponse ->
        MovieUiModel(movie = MovieResponseMapper.mapToDomain(movieResponse))
    }
}

object MovieResponseMapper {
    fun mapToDomain(movieResponse: MovieResponse): Movie {
        return with(movieResponse) {
            Movie(
                movieId = id,
                title = title,
                overview = overview,
                posterPath = posterPath,
                backdropPath = backdropPath,
                rating = rating,
                releaseDate = releaseDate
            )
        }
    }
}