package com.example.tmdbapplication.presentation.movielists

import com.example.tmdbapplication.presentation.model.MovieUiModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MovieListsView : MvpView {

    @StateStrategyType(value = AddToEndStrategy::class)
    fun onFirstOpen(movieList: List<MovieUiModel>)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun onNewMovies(movieList: List<MovieUiModel>)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setProgressBarVisibility(isVisible: Boolean)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun notifyWatchlistFlagChanged(position: Int)
}