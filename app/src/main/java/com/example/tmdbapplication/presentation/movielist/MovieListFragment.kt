package com.example.tmdbapplication.presentation.movielist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tmdbapplication.R
import com.example.tmdbapplication.data.paging.MovieRequestType
import com.example.tmdbapplication.databinding.FragmentMovieListBinding
import com.example.tmdbapplication.presentation.model.Status
import com.example.tmdbapplication.presentation.movielist.list.AdapterType
import com.example.tmdbapplication.presentation.movielist.list.MovieListAdapter
import com.example.tmdbapplication.presentation.movielist.pager.TrendingItemDecoration
import com.example.tmdbapplication.presentation.movielist.pager.TrendingOnPageChangeCallback
import com.example.tmdbapplication.presentation.movielist.pager.TrendingPageTransformer
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val movieListViewModel: MovieListViewModel by viewModels()

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val movieListAdapter
        get() = MovieListAdapter(
            AdapterType.COMMON,
            onMovieClick = { movie ->
                findNavController().navigate(
                    MovieListFragmentDirections.actionHomeToDetails(movie)
                )
            }
        ) { movie -> movieListViewModel.manageSelectedInWatchlist(movie) }

    private lateinit var trendingMovieAdapter: MovieListAdapter

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
        initTrendingAdapter()
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

    private fun initTrendingAdapter() {
        trendingMovieAdapter = MovieListAdapter(
            AdapterType.TRENDING,
            onMovieClick = { movie ->
                findNavController().navigate(
                    MovieListFragmentDirections.actionHomeToDetails(movie))
            }
        ) { }
    }

    private fun initListeners() {
        binding.allPopular.root.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections
                    .actionHomeToPagedList(MovieRequestType.POPULAR)
            )
        }

        binding.allNowPlaying.root.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections
                    .actionHomeToPagedList(MovieRequestType.NOW_PLAYING)
            )
        }

        binding.allUpcoming.root.setOnClickListener {
            findNavController().navigate(
                MovieListFragmentDirections
                    .actionHomeToPagedList(MovieRequestType.UPCOMING)
            )
        }
    }

    private fun observeViewModel() {
        movieListViewModel.movies.observe(viewLifecycleOwner) { tripleList ->
            (binding.popularMoviesList.adapter as MovieListAdapter).submitList(tripleList.first)
            (binding.nowPlayingMoviesList.adapter as MovieListAdapter).submitList(tripleList.second)
            (binding.upcomingMoviesList.adapter as MovieListAdapter).submitList(tripleList.third)
        }

        movieListViewModel.status.observe(viewLifecycleOwner) { status ->
            binding.movieListProgress.layoutProgressBar.setVisibility(status == Status.LOADING)
            if (status == Status.ERROR) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        // TODO: 8/18/21 ItemDecoration in landscape mode
        movieListViewModel.trendingMovies.observe(viewLifecycleOwner) { list ->
            trendingMovieAdapter.submitList(list)
            binding.trendingMoviesPager.apply {
                offscreenPageLimit = 1
                setPageTransformer(TrendingPageTransformer())
                addItemDecoration(TrendingItemDecoration())
                registerOnPageChangeCallback(
                    TrendingOnPageChangeCallback(this, list)
                )
                adapter = trendingMovieAdapter
                setCurrentItem(1, false)
            }
        }
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}