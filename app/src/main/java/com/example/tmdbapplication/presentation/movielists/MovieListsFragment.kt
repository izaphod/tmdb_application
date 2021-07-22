package com.example.tmdbapplication.presentation.movielists

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject
import javax.inject.Provider
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import com.example.tmdbapplication.R
import com.example.tmdbapplication.TmdbApplication
import com.example.tmdbapplication.databinding.FragmentMovieListsBinding
import com.example.tmdbapplication.presentation.model.MovieUiModel
import com.example.tmdbapplication.presentation.movielists.list.MovieItemAdapter
import com.example.tmdbapplication.presentation.movielists.list.onScrollToMiddle

class MovieListsFragment : MvpAppCompatFragment(R.layout.fragment_movie_lists), MovieListsView {

    @Inject
    lateinit var presenterProvider: Provider<MovieListsPresenter>
    private val presenter: MovieListsPresenter by moxyPresenter { presenterProvider.get() }

    private lateinit var movieLayoutManager: LinearLayoutManager

    private var _binding: FragmentMovieListsBinding? = null
    private val binding get() = _binding!!

    private val movieItemAdapter = MovieItemAdapter(
        movieList = mutableListOf(),
        onMovieClick = {

        }
    ) { movie, position ->
        presenter.onWatchlistMenuPressed(movie, position)
    }

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
        _binding = FragmentMovieListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewPopularMovies.adapter = movieItemAdapter
        movieLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPopularMovies.layoutManager = movieLayoutManager

        binding.recyclerViewPopularMovies.onScrollToMiddle(movieLayoutManager, presenter.lastLoadedPage) {
            presenter.onMovieListScrolled(it)
            binding.recyclerViewPopularMovies.clearOnScrollListeners()
            Log.d(TAG, "onScrolledTo: page = $it")
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.watchlist -> {
                    findNavController()
                        .navigate(
                            MovieListsFragmentDirections
                                .actionMovieListsFragmentToWatchlistFragment()
                        )
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        Log.d(TAG, "onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun onFirstOpen(movieList: List<MovieUiModel>) {
        movieItemAdapter.appendMovies(movieList)
    }

    override fun onNewMovies(movieList: List<MovieUiModel>) {
        movieItemAdapter.appendMovies(movieList)
        binding.recyclerViewPopularMovies.onScrollToMiddle(movieLayoutManager, presenter.lastLoadedPage) {
            presenter.onMovieListScrolled(it)
            binding.recyclerViewPopularMovies.clearOnScrollListeners()
            Log.d(TAG, "onScrolledTo: page = $it")
        }
    }

    override fun setProgressBarVisibility(isVisible: Boolean) {
        binding.progressBar.layoutProgressBar.isVisible = isVisible
    }

    override fun notifyWatchlistFlagChanged(position: Int) {
        movieItemAdapter.notifyItemChanged(position)
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}