package com.example.tmdbapplication.presentation.searchmovie

import androidx.paging.PagingData
import com.example.tmdbapplication.presentation.model.MovieViewModel
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SearchMovieView : MvpView {

    @StateStrategyType(value = AddToEndStrategy::class)
    fun showSearchResult(pagingData: PagingData<MovieViewModel>)

    @StateStrategyType(value = AddToEndStrategy::class)
    fun setEmptyScreenVisibility(isVisible: Boolean)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showEmptyQueryToast()
}