package com.example.tmdbapplication.presentation.watchlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.TmdbApplication
import com.example.tmdbapplication.databinding.FragmentWatchlistBinding
import com.example.tmdbapplication.presentation.model.WatchlistViewModel
import com.example.tmdbapplication.presentation.movielists.list.MovieItemAdapter
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
        itemList = mutableListOf(),
        onMovieClick = { movie ->

        }
    ) { movie, position ->

    }

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

        binding.recyclerViewWatchlist.adapter = adapter
        binding.recyclerViewWatchlist.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showWatchlist(movies: List<WatchlistViewModel>) {
        adapter.replaceMovies(movies)
    }

    override fun setProgressBarVisibility(isVisible: Boolean) {
        binding.progressBar.layoutProgressBar.isVisible = isVisible
    }

    override fun setEmptyScreenVisibility(isVisible: Boolean) {
        binding.textViewEmptyWatchlist.isVisible = isVisible
    }
}