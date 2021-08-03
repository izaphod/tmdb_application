package com.example.tmdbapplication.presentation.watchlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.TmdbApplication
import com.example.tmdbapplication.databinding.FragmentWatchlistBinding
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.watchlist.list.MovieItemAdapter
import com.example.tmdbapplication.util.setVisibility
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class WatchlistFragment : MvpAppCompatFragment(R.layout.fragment_watchlist), WatchlistView {

    @Inject
    lateinit var presenterProvider: Provider<WatchlistPresenter>
    private val presenter: WatchlistPresenter by moxyPresenter { presenterProvider.get() }

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val adapter = MovieItemAdapter(
        movieList = mutableListOf(),
        onMovieClick = { movie ->
            findNavController().navigate(
                WatchlistFragmentDirections
                    .actionWatchlistFragmentToMovieDetailsFragment(movie)
            )
        }
    ) { movie -> presenter.onItemWatchlistPressed(movie) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TmdbApplication.instance.appComponent?.injectFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.watchlistList.adapter = adapter
        binding.watchlistList.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNewMovie(movies: List<MovieViewModel>) {
        adapter.replaceMovies(movies)
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        binding.progress.layoutProgressBar.setVisibility(isVisible)
    }

    override fun setEmptyScreenVisibility(isVisible: Boolean) {
        binding.emptyWatchlist.setVisibility(isVisible)
    }

    companion object {
        private const val TAG = "WatchlistFragment"
    }
}