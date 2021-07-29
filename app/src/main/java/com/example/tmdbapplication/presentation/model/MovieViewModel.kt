package com.example.tmdbapplication.presentation.model

data class MovieViewModel(
    val state: State,
    val movies: List<WatchlistViewModel>
) {

    sealed class State {
        object Loading : State()
        class Error(val msg: String) : State()
        object Content : State()
        object Empty : State()
    }
}