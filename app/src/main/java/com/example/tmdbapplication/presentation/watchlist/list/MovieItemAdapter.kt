package com.example.tmdbapplication.presentation.watchlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.movielist.list.MovieItemViewHolder

class MovieItemAdapter(
    private var movieList: MutableList<MovieViewModel>,
    private val onMovieClick: (movie: MovieViewModel) -> Unit,
    private val onWatchlistMenuClick: (movie: MovieViewModel) -> Unit
) : RecyclerView.Adapter<MovieItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieItemViewHolder(
            itemView = view,
            onMovieClick = onMovieClick,
            onWatchlistMenuClick = onWatchlistMenuClick
        )
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    override fun getItemCount(): Int = movieList.size

    // TODO: 7/30/21 Old items remain in the movieList after replaceMovies()
    fun replaceMovies(movies: List<MovieViewModel>) {
        this.movieList.clear()
        this.movieList.addAll(movies)
        notifyDataSetChanged()
    }

    fun appendMovie(movie: MovieViewModel) {
        this.movieList.add(movie)
        notifyItemRangeInserted(this.movieList.size, movieList.size - 1)
    }
}