package com.example.tmdbapplication.presentation.movielist.pager

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.TrendingItemBinding
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.moviedetails.MovieDetailsFragment

class TrendingItemViewHolder(
    itemView: View,
    private val onMovieClick: (movieViewModel: MovieViewModel) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding = TrendingItemBinding.bind(itemView)
    private var movieViewModel: MovieViewModel? = null

    private val onMovieClickListener = View.OnClickListener {
        movieViewModel?.let { onMovieClick(it) }
    }

    fun bind(movieViewModel: MovieViewModel) {
        this.movieViewModel = movieViewModel
        val backdrop =
            if (movieViewModel.movie.posterPath.isNullOrEmpty()) null
            else MovieDetailsFragment.BACKDROP_FORMATTED_PATH + movieViewModel.movie.backdropPath
        Glide.with(itemView)
            .load(backdrop)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .centerCrop()
            .into(binding.trendingBackdrop)

        binding.trendingTitle.text = movieViewModel.movie.title

        itemView.setOnClickListener(onMovieClickListener)
    }
}
