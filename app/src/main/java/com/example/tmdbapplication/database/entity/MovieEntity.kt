package com.example.tmdbapplication.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.presentation.model.MovieUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "movie_database")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "movie_id") val movieId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "poster_path") val posterPath: String? = null,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String? = null,
    @ColumnInfo(name = "rating") val rating: Float,
    @ColumnInfo(name = "release_date") val releaseDate: String? = null,
    @ColumnInfo(name = "page") val page: Int
) : Parcelable

fun List<MovieEntity>.asUiModel(): List<MovieUiModel> {
    return map { entity ->
        MovieUiModel(movie = MovieEntityMapper.mapToDomain(entity))
    }
}

object MovieEntityMapper {
    fun mapToDomain(entity: MovieEntity): Movie {
        return with(entity) {
            Movie(
                movieId = movieId,
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