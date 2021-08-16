package com.example.tmdbapplication.presentation.movielist.list

import android.os.Build
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.MovieItemBinding
import com.example.tmdbapplication.di.module.GlideApp
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.util.formatPosterPath

class MovieItemViewHolder(
    itemView: View,
    private val onMovieClick: (movieViewModel: MovieViewModel) -> Unit,
    private val onWatchlistMenuClick: (movieViewModel: MovieViewModel) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding = MovieItemBinding.bind(itemView)
    private var movieViewModel: MovieViewModel? = null

    private val onMovieClickListener = View.OnClickListener {
        movieViewModel?.let { onMovieClick(it) }
    }

    fun bind(movieViewModel: MovieViewModel) {
        this.movieViewModel = movieViewModel
        val posterPath =
            if (movieViewModel.movie.posterPath.isNullOrEmpty()) null
            else movieViewModel.movie.posterPath.formatPosterPath()
        GlideApp.with(itemView)
            .load(posterPath)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .centerCrop()
            .into(binding.moviePoster)
        binding.movieRating.userScore = movieViewModel.movie.rating
        binding.movieTitle.text = movieViewModel.movie.title
        binding.movieReleaseDate.text = movieViewModel.movie.releaseDate
        binding.imageViewMenu.setOnClickListener {
            showMenu(it, movieViewModel)
        }
        itemView.setOnClickListener(onMovieClickListener)
    }

    private fun showMenu(view: View, movieViewModel: MovieViewModel) {
        val popup = PopupMenu(itemView.context, view)
        popup.menuInflater.inflate(R.menu.menu_movie_item_popup, popup.menu)
        popup.menu.findItem(R.id.watchlist).setIcon(
            if (movieViewModel.isInWatchlist) R.drawable.ic_round_watchlist_true_24
            else R.drawable.ic_round_watchlist_false_24
        )
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.watchlist -> {
                    val text: String
                    onWatchlistMenuClick.invoke(movieViewModel)
                    if (movieViewModel.isInWatchlist) {
                        text = itemView.resources.getString(
                            R.string.deleted_from_watchlist,
                            movieViewModel.movie.title
                        )
                        it.setIcon(R.drawable.ic_round_watchlist_false_24)
                        movieViewModel.isInWatchlist = false
                    } else {
                        text = itemView.resources.getString(
                            R.string.added_to_watchlist,
                            movieViewModel.movie.title
                        )
                        it.setIcon(R.drawable.ic_round_watchlist_true_24)
                        movieViewModel.isInWatchlist = true
                    }
                    Toast.makeText(itemView.context, text, Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }
        popup.show()
    }
}
