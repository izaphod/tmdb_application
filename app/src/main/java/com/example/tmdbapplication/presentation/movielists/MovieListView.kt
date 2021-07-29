package com.example.tmdbapplication.presentation.movielists

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MovieListView : MvpView {

    @StateStrategyType(value = AddToEndStrategy::class)
    fun notifyWatchlistFlagChanged(position: Int)
}