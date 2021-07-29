package com.example.tmdbapplication.presentation.model

import android.os.Parcelable
import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import com.example.tmdbapplication.domain.model.Movie
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchlistViewModel(
    val movie: Movie,
    var isInWatchlist: Boolean = false
) : Parcelable

fun List<Movie>.asWatchlistViewModel(): List<WatchlistViewModel> {
    return map { movie ->
        WatchlistViewModel(movie = movie, isInWatchlist = false)
    }
}

fun WatchlistViewModel.asWatchlistEntity(): WatchlistEntity {
    return WatchlistEntity(movieId = this.movie.movieId)
}

