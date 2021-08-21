package com.example.tmdbapplication.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.FragmentWatchlistBinding
import com.example.tmdbapplication.presentation.model.Status
import com.example.tmdbapplication.presentation.movielist.MovieListViewModel
import com.example.tmdbapplication.presentation.movielist.list.AdapterType
import com.example.tmdbapplication.presentation.movielist.list.MovieListAdapter
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WatchlistFragment : Fragment(R.layout.fragment_watchlist) {

    private val watchListViewModel: WatchlistViewModel by viewModels()
    private val movieListViewModel: MovieListViewModel by viewModels()

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val movieItemAdapter = MovieListAdapter(
        AdapterType.COMMON,
        onMovieClick = { movie ->
            findNavController().navigate(
                WatchlistFragmentDirections.actionWatchlistToDetails(movie)
            )
        }
    ) { movie -> watchListViewModel.manageSelectedInWatchlist(movie) }

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
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        binding.watchlistList.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = movieItemAdapter
        }
    }

    private fun observeViewModel() {
        watchListViewModel
            .movies
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { movieItemAdapter.submitList(it) }
        watchListViewModel
            .status
            .observe(viewLifecycleOwner) { status ->
            binding.watchlistProgress.layoutProgressBar.setVisibility(status == Status.LOADING)
            binding.emptyWatchlist.setVisibility(status == Status.ERROR || status == Status.EMPTY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}