package com.example.tmdbapplication.presentation.movielist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.tmdbapplication.R
import com.example.tmdbapplication.presentation.model.MovieViewModel

class MoviePagingAdapter(
    private val onMovieClick: (movie: MovieViewModel) -> Unit,
    private val onWatchlistMenuClick: (movie: MovieViewModel) -> Unit
) : PagingDataAdapter<MovieViewModel, MovieItemViewHolder>(
    COMPARATOR
) {

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieItemViewHolder(view, onMovieClick, onWatchlistMenuClick)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<MovieViewModel>() {
            override fun areItemsTheSame(
                oldItem: MovieViewModel,
                newItem: MovieViewModel
            ): Boolean {
                return oldItem.movie.movieId == newItem.movie.movieId
            }

            override fun areContentsTheSame(
                oldItem: MovieViewModel,
                newItem: MovieViewModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}