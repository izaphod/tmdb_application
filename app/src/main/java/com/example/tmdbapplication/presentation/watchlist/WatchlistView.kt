package com.example.tmdbapplication.presentation.watchlist

import com.example.tmdbapplication.presentation.model.MovieViewModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface WatchlistView : MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun onNewMovie(movies: List<MovieViewModel>)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setProgressBarVisibility(isVisible: Boolean)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun setEmptyScreenVisibility(isVisible: Boolean)
}