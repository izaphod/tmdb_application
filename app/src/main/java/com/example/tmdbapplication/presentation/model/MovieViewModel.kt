package com.example.tmdbapplication.presentation.model

import android.os.Parcelable
import com.example.tmdbapplication.data.database.entity.WatchlistEntity
import com.example.tmdbapplication.domain.model.Movie
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieViewModel(
    val movie: Movie,
    var isInWatchlist: Boolean = false
) : Parcelable

fun List<Movie>.asMovieViewModels(): List<MovieViewModel> {
    return map { movie -> movie.asMovieViewModels() }
}

fun Movie.asMovieViewModels(): MovieViewModel {
    return MovieViewModel(movie = this)
}

fun MovieViewModel.asWatchlistEntity(): WatchlistEntity {
    return WatchlistEntity(movieId = this.movie.movieId)
}


