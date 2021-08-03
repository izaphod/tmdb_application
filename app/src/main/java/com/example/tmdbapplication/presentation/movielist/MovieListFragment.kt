package com.example.tmdbapplication.presentation.movielist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
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
import com.example.tmdbapplication.util.setVisibility

class MovieListFragment : MvpAppCompatFragment(R.layout.fragment_movie_list), MovieListView {

    @Inject
    lateinit var presenterProvider: Provider<MovieListPresenter>
    private val presenter: MovieListPresenter by moxyPresenter { presenterProvider.get() }

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val movieItemAdapter = MoviePagingAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                MovieListFragmentDirections
                    .actionMovieListFragmentToMovieDetailsFragment(movie)
            )
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
        addLoadStateListener()
        Log.d(TAG, "onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun showMovies(pagingData: PagingData<MovieViewModel>) {
        movieItemAdapter.submitData(lifecycle, pagingData)
        Log.d(TAG, "onNewMovies")
    }

    private fun initRecyclerView() {
        binding.popularMoviesList.adapter = movieItemAdapter
        binding.popularMoviesList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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

    private fun addLoadStateListener() {
        movieItemAdapter.addLoadStateListener { loadStates ->
            if (loadStates.refresh is LoadState.Loading) {
                binding.progress.layoutProgressBar.setVisibility(true)
            } else {
                binding.progress.layoutProgressBar.setVisibility(false)
                // TODO: 7/31/21 add error state screen
                val errorState = when {
                    loadStates.prepend is LoadState.Error -> loadStates.prepend as LoadState.Error
                    loadStates.append is LoadState.Error -> loadStates.append as LoadState.Error
                    loadStates.refresh is LoadState.Error -> loadStates.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this.context, it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}