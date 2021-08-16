package com.example.tmdbapplication.presentation.model

import android.os.Parcelable
import com.example.tmdbapplication.domain.model.Movie
import com.example.tmdbapplication.domain.usecase.IsInWatchlistUseCase
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieViewModel(
    val movie: Movie,
    var isInWatchlist: Boolean = false
) : Parcelable

fun List<Movie>.asMovieViewModels(): List<MovieViewModel> {
    return map { movie -> movie.asMovieViewModel() }
}

fun Movie.asMovieViewModel(): MovieViewModel {
    return MovieViewModel(movie = this)
}

suspend fun Triple<List<Movie>, List<Movie>, List<Movie>>.asMovieViewModels(
    isInWatchlistUseCase: IsInWatchlistUseCase
): Triple<List<MovieViewModel>, List<MovieViewModel>, List<MovieViewModel>> {
    return Triple(
        first.asMovieViewModels(isInWatchlistUseCase),
        second.asMovieViewModels(isInWatchlistUseCase),
        third.asMovieViewModels(isInWatchlistUseCase)
    )
}

suspend fun List<Movie>.asMovieViewModels(
    isInWatchlistUseCase: IsInWatchlistUseCase
): List<MovieViewModel> {
    return map { movie -> movie.asMovieViewModel(isInWatchlistUseCase) }
}

suspend fun Movie.asMovieViewModel(isInWatchlistUseCase: IsInWatchlistUseCase): MovieViewModel {
    val movie = MovieViewModel(movie = this)
    movie.isInWatchlist = isInWatchlistUseCase.execute(movieId)
    return movie
}

fun List<MovieViewModel>.expandForViewPagerList(): MutableList<MovieViewModel> {
    val list = this.toMutableList()
    list.add(0, last())
    list.add(first())
    return list
}


