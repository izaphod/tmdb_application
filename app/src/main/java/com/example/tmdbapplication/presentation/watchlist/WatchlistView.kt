package com.example.tmdbapplication.presentation.watchlist

import com.example.tmdbapplication.presentation.model.WatchlistViewModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

interface WatchlistView : MvpView {

    @StateStrategyType(value = AddToEndStrategy::class)
    fun showWatchlist(movies: List<WatchlistViewModel>)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setProgressBarVisibility(isVisible: Boolean)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setEmptyScreenVisibility(isVisible: Boolean)
}