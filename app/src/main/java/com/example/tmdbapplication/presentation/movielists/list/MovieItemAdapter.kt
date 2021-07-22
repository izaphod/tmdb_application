package com.example.tmdbapplication.presentation.movielists.list

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.MovieItemBinding
import com.example.tmdbapplication.presentation.model.MovieUiModel

class MovieItemAdapter(
    private var movieList: MutableList<MovieUiModel>,
    private val onMovieClick: (movie: MovieUiModel) -> Unit,
    private val onWatchlistMenuClick: (movie: MovieUiModel, position: Int) -> Unit
) : RecyclerView.Adapter<MovieItemAdapter.MovieItemViewHolder>() {

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
        holder.bind(movieList[position], position)
    }

    override fun getItemCount(): Int = movieList.size

    fun appendMovies(movieList: List<MovieUiModel>) {
        this.movieList.addAll(movieList)
        val list = this.movieList.distinctBy { it.movie }
        this.movieList = list as MutableList<MovieUiModel>
        notifyItemRangeInserted(this.movieList.size, movieList.size - 1)
    }

    fun replaceMovies(movieList: List<MovieUiModel>) {
        this.movieList.clear()
        this.movieList.addAll(movieList)
        notifyDataSetChanged()
    }

    fun appendMovie(movie: MovieUiModel) {
        this.movieList.add(movie)
        notifyItemRangeInserted(this.movieList.size, movieList.size - 1)
    }

    class MovieItemViewHolder(
        itemView: View,
        private val onMovieClick: (movieUiModel: MovieUiModel) -> Unit,
        private val onWatchlistMenuClick: (movieUiModel: MovieUiModel, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = MovieItemBinding.bind(itemView)
        private var movieUiModel: MovieUiModel? = null

        private val onMovieClickListener = View.OnClickListener {
            movieUiModel?.let { onMovieClick(it) }
        }

        fun bind(movieUiModel: MovieUiModel, position: Int) {
            this.movieUiModel = movieUiModel
            val posterPath =
                if (movieUiModel.movie.posterPath.isNullOrEmpty()) null
                else POSTER_FORMATTED_PATH + movieUiModel.movie.posterPath
            Glide.with(itemView)
                .load(posterPath)
                .apply(RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                )
                .centerCrop()
                .into(binding.moviePoster)
            binding.movieRating.userScore = movieUiModel.movie.rating
            binding.movieTitle.text = movieUiModel.movie.title
            binding.movieReleaseDate.text = movieUiModel.movie.releaseDate
            binding.imageViewMenu.setOnClickListener {
                showMenu(it, movieUiModel, position)
            }
            itemView.setOnClickListener(onMovieClickListener)
        }

        private fun showMenu(view: View, movieUiModel: MovieUiModel, position: Int) {
            val popup = PopupMenu(itemView.context, view)
            popup.menuInflater.inflate(R.menu.menu_movie_item_popup, popup.menu)
            popup.menu.findItem(R.id.watchlist).setIcon(
                if (movieUiModel.isInWatchlist) R.drawable.ic_round_watchlist_true_24
                else R.drawable.ic_round_watchlist_false_24
            )
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.watchlist -> {
                        var text = ""
                        onWatchlistMenuClick.invoke(movieUiModel, position)
                        if (movieUiModel.isInWatchlist) {
                            text = itemView.resources.getString(R.string.deleted_from_watchlist, movieUiModel.movie.title)
                            it.setIcon(R.drawable.ic_round_watchlist_false_24)
                            movieUiModel.isInWatchlist = false
                        } else {
                            text = itemView.resources.getString(R.string.added_to_watchlist, movieUiModel.movie.title)
                            it.setIcon(R.drawable.ic_round_watchlist_true_24)
                            movieUiModel.isInWatchlist = true
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

        companion object {
            const val POSTER_FORMATTED_PATH = "https://image.tmdb.org/t/p/w342"
        }
    }
}