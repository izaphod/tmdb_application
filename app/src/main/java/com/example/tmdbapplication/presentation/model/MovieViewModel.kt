package com.example.tmdbapplication.presentation.model

import android.os.Parcelable
import com.example.tmdbapplication.domain.model.Movie
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieViewModel(
    val movie: Movie,
    var isInWatchlist: Boolean = false
) : Parcelable

fun Triple<List<Movie>, List<Movie>, List<Movie>>.asMovieViewModels():
        Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>> {
    return Triple(
        first.asMovieViewModels(),
        second.asMovieViewModels(),
        third.asMovieViewModels()
    )
}

fun List<Movie>.asMovieViewModels(): List<MovieViewModel> {
    return map { movie -> movie.asMovieViewModel() }
}

fun Movie.asMovieViewModel(): MovieViewModel {
    return MovieViewModel(movie = this)
}


