package com.example.tmdbapplication.presentation.movielist

import androidx.paging.PagingData
import com.example.tmdbapplication.presentation.model.MovieViewModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MovieListView : MvpView {

    @StateStrategyType(value = AddToEndStrategy::class)
    fun onNewMovies(pagingData: PagingData<MovieViewModel>)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setProgressBarVisibility(isVisible: Boolean)
}