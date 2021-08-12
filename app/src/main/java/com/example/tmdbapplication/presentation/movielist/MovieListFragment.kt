package com.example.tmdbapplication.presentation.movielist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.FragmentMovieListBinding
import com.example.tmdbapplication.presentation.watchlist.list.MovieListAdapter
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val movieListViewModel: MovieListViewModel by viewModels()

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val movieListAdapter
        get() = MovieListAdapter(
                onMovieClick = { movie ->
                    findNavController().navigate(
                        MovieListFragmentDirections
                            .actionMovieListFragmentToMovieDetailsFragment(movie)
                    )
                }
            ) { movie -> movieListViewModel.manageSelectedInWatchlist(movie) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        observeViewModel()
        Log.d(TAG, "onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView")
    }

    private fun initViews() {
        binding.popularMoviesList.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = movieListAdapter
        }

        binding.nowPlayingMoviesList.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = movieListAdapter
        }

        binding.upcomingMoviesList.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = movieListAdapter
        }
    }

    private fun initListeners() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.watchlist -> {
                    findNavController()
                        .navigate(
                            MovieListFragmentDirections
                                .actionMovieListFragmentToWatchlistFragment()
                        )
                    return@setOnMenuItemClickListener true
                }
                R.id.search -> {
                    findNavController().navigate(
                        MovieListFragmentDirections
                            .actionMovieListFragmentToSearchMovieFragment()
                    )
                }
            }
            false
        }
    }

    private fun observeViewModel() {
        movieListViewModel.movies.observe(viewLifecycleOwner, Observer { tripleList ->
            (binding.popularMoviesList.adapter as MovieListAdapter).submitList(tripleList.first)
            (binding.nowPlayingMoviesList.adapter as MovieListAdapter).submitList(tripleList.second)
            (binding.upcomingMoviesList.adapter as MovieListAdapter).submitList(tripleList.third)
        })

        movieListViewModel.status.observe(viewLifecycleOwner, Observer { status ->
            binding.movieListProgress.layoutProgressBar.setVisibility(status == Status.LOADING)
            if (status == Status.ERROR) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}