package com.example.tmdbapplication.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.FragmentWatchlistBinding
import com.example.tmdbapplication.presentation.movielist.Status
import com.example.tmdbapplication.presentation.watchlist.list.MovieListAdapter
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WatchlistFragment : Fragment(R.layout.fragment_watchlist) {

    private val watchListViewModel: WatchlistViewModel by viewModels()

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val movieItemAdapter = MovieListAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                WatchlistFragmentDirections
                    .actionWatchlistFragmentToMovieDetailsFragment(movie)
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
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.navigationIcon = ContextCompat
            .getDrawable(requireContext(), R.drawable.ic_arrow_back)

        binding.watchlistList.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = movieItemAdapter
        }
    }

    private fun observeViewModel() {
        watchListViewModel.movies.observe(viewLifecycleOwner, Observer {
            movieItemAdapter.submitList(it)
        })

        watchListViewModel.status.observe(viewLifecycleOwner, Observer { status ->
            binding.watchlistProgress.layoutProgressBar.setVisibility(status == Status.LOADING)
            binding.emptyWatchlist.setVisibility(status == Status.ERROR || status == Status.EMPTY)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "WatchlistFragment"
    }
}