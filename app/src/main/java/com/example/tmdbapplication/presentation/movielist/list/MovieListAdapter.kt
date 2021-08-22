package com.example.tmdbapplication.presentation.movielist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.movielist.viewpager.TrendingItemViewHolder
import com.example.tmdbapplication.presentation.pagedmovie.list.MoviePagingAdapter

class MovieListAdapter(
    private val adapterType: AdapterType,
    private val onMovieClick: (movie: MovieViewModel) -> Unit,
    private val onWatchlistMenuClick: (movie: MovieViewModel) -> Unit
) : ListAdapter<MovieViewModel, RecyclerView.ViewHolder>(MoviePagingAdapter.COMPARATOR) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (adapterType) {
                AdapterType.COMMON -> (holder as MovieItemViewHolder).bind(it)
                AdapterType.TRENDING -> (holder as TrendingItemViewHolder).bind(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (adapterType) {
            AdapterType.COMMON -> 0
            AdapterType.TRENDING -> 1
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val view = layoutInflater.inflate(R.layout.movie_item, parent, false)
                MovieItemViewHolder(
                    itemView = view,
                    onMovieClick = onMovieClick,
                    onWatchlistMenuClick = onWatchlistMenuClick
                )
            }
            1 -> {
                val view = layoutInflater.inflate(R.layout.trending_item, parent, false)
                TrendingItemViewHolder(itemView = view, onMovieClick = onMovieClick)
            }
            else -> {
                throw  RuntimeException("Unsupported view type = $viewType")
            }
        }
    }
}