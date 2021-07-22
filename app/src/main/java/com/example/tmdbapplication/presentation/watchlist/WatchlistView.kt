package com.example.tmdbapplication.presentation.watchlist

import com.example.tmdbapplication.presentation.model.MovieUiModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

interface WatchlistView : MvpView {

    @StateStrategyType(value = AddToEndStrategy::class)
    fun showWatchlist(movies: List<MovieUiModel>)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setProgressBarVisibility(isVisible: Boolean)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setEmptyScreenVisibility(isVisible: Boolean)
}