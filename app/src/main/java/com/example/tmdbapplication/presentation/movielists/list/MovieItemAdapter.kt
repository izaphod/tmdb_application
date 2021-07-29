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
import com.example.tmdbapplication.presentation.model.WatchlistViewModel

class MovieItemAdapter(
    private var itemList: MutableList<WatchlistViewModel>,
    private val onMovieClick: (movie: WatchlistViewModel) -> Unit,
    private val onWatchlistMenuClick: (movie: WatchlistViewModel, position: Int) -> Unit
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
        holder.bind(itemList[position], position)
    }

    override fun getItemCount(): Int = itemList.size

    fun appendMovies(movieList: List<WatchlistViewModel>) {
        this.itemList.addAll(movieList)
        val list = this.itemList.distinctBy { it.movie }
        this.itemList = list as MutableList<WatchlistViewModel>
        notifyItemRangeInserted(this.itemList.size, list.size - 1)
    }

    fun replaceMovies(list: List<WatchlistViewModel>) {
        this.itemList.clear()
        this.itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun appendMovie(movie: WatchlistViewModel) {
        this.itemList.add(movie)
        notifyItemRangeInserted(this.itemList.size, itemList.size - 1)
    }

    class MovieItemViewHolder(
        itemView: View,
        private val onMovieClick: (watchlistViewModel: WatchlistViewModel) -> Unit,
        private val onWatchlistMenuClick: (watchlistViewModel: WatchlistViewModel, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = MovieItemBinding.bind(itemView)
        private var watchlistViewModel: WatchlistViewModel? = null

        private val onMovieClickListener = View.OnClickListener {
            watchlistViewModel?.let { onMovieClick(it) }
        }

        fun bind(watchlistViewModel: WatchlistViewModel, position: Int) {
            this.watchlistViewModel = watchlistViewModel
            val posterPath =
                if (watchlistViewModel.movie.posterPath.isNullOrEmpty()) null
                else POSTER_FORMATTED_PATH + watchlistViewModel.movie.posterPath
            Glide.with(itemView)
                .load(posterPath)
                .apply(RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                )
                .centerCrop()
                .into(binding.moviePoster)
            binding.movieRating.userScore = watchlistViewModel.movie.rating
            binding.movieTitle.text = watchlistViewModel.movie.title
            binding.movieReleaseDate.text = watchlistViewModel.movie.releaseDate
            binding.imageViewMenu.setOnClickListener {
                showMenu(it, watchlistViewModel, position)
            }
            itemView.setOnClickListener(onMovieClickListener)
        }

        private fun showMenu(view: View, watchlistViewModel: WatchlistViewModel, position: Int) {
            val popup = PopupMenu(itemView.context, view)
            popup.menuInflater.inflate(R.menu.menu_movie_item_popup, popup.menu)
            popup.menu.findItem(R.id.watchlist).setIcon(
                if (watchlistViewModel.isInWatchlist) R.drawable.ic_round_watchlist_true_24
                else R.drawable.ic_round_watchlist_false_24
            )
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.watchlist -> {
                        val text: String
                        onWatchlistMenuClick.invoke(watchlistViewModel, position)
                        if (watchlistViewModel.isInWatchlist) {
                            text = itemView.resources.getString(R.string.deleted_from_watchlist, watchlistViewModel.movie.title)
                            it.setIcon(R.drawable.ic_round_watchlist_false_24)
                            watchlistViewModel.isInWatchlist = false
                        } else {
                            text = itemView.resources.getString(R.string.added_to_watchlist, watchlistViewModel.movie.title)
                            it.setIcon(R.drawable.ic_round_watchlist_true_24)
                            watchlistViewModel.isInWatchlist = true
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