package com.example.tmdbapplication.presentation.movielist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject
import javax.inject.Provider
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import com.example.tmdbapplication.R
import com.example.tmdbapplication.TmdbApplication
import com.example.tmdbapplication.databinding.FragmentMovieListBinding
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.movielist.list.MoviePagingAdapter

class MovieListFragment : MvpAppCompatFragment(R.layout.fragment_movie_list), MovieListView {

    @Inject
    lateinit var presenterProvider: Provider<MovieListPresenter>
    private val presenter: MovieListPresenter by moxyPresenter { presenterProvider.get() }

    private lateinit var movieLayoutManager: LinearLayoutManager

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val movieItemAdapter = MoviePagingAdapter(
        onMovieClick = {

        }
    ) { movie -> presenter.onItemWatchlistPressed(movie) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TmdbApplication.instance.appComponent?.injectFragment(this)
        Log.d(TAG, "onAttach")
    }

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
        initRecyclerView()
        initListeners()
        Log.d(TAG, "onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun onNewMovies(pagingData: PagingData<MovieViewModel>) {
        movieItemAdapter.submitData(lifecycle, pagingData)
        Log.d(TAG, "onNewMovies")
    }

    override fun setProgressBarVisibility(isVisible: Boolean) {
        binding.progressBar.layoutProgressBar.isVisible = isVisible
        Log.d(TAG, "setProgressBarVisibility: $isVisible")
    }

    private fun initRecyclerView() {
        binding.recyclerViewPopularMovies.adapter = movieItemAdapter
        movieLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPopularMovies.layoutManager = movieLayoutManager
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
            }
            false
        }
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}