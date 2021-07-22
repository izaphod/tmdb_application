package com.example.tmdbapplication.presentation.model

import android.os.Parcelable
import com.example.tmdbapplication.database.entity.WatchlistEntity
import com.example.tmdbapplication.domain.model.Movie
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieUiModel(
    val movie: Movie,
    var isInWatchlist: Boolean = false
) : Parcelable

object UiModelMapper {
    fun mapToWatchlistEntity(uiModel: MovieUiModel): WatchlistEntity {
        return with(uiModel) {
            WatchlistEntity(movieId = movie.movieId)
        }
    }
}

