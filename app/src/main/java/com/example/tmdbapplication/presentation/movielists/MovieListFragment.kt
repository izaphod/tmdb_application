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
import com.example.tmdbapplication.databinding.FragmentMovieListBinding
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.movielists.list.MovieItemAdapter
import com.example.tmdbapplication.presentation.movielists.list.onScrollToEnd
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable

class MovieListFragment : MvpAppCompatFragment(R.layout.fragment_movie_list), MovieListView {

    @Inject
    lateinit var presenterProvider: Provider<MovieListPresenter>
    private val presenter: MovieListPresenter by moxyPresenter { presenterProvider.get() }

    private lateinit var movieLayoutManager: LinearLayoutManager

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val movieItemAdapter = MovieItemAdapter(
        itemList = mutableListOf(),
        onMovieClick = {

        }
    ) { movie, position ->
        presenter.onItemWatchlistPressed(movie, position)
    }

    private var viewModelDisposable: Disposable? = null

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
        setProgressBarVisibility(true)
        initRecyclerView()
        initListeners()
        subscribeToViewModels()
        Log.d(TAG, "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModels()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        unsubscribeFromViewModels()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unsubscribeFromViewModels()
        Log.d(TAG, "onDestroyView")
    }

    override fun notifyWatchlistFlagChanged(position: Int) {
        movieItemAdapter.notifyItemChanged(position)
    }

    private fun initRecyclerView() {
        binding.recyclerViewPopularMovies.adapter = movieItemAdapter
        movieLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPopularMovies.layoutManager = movieLayoutManager
        binding.recyclerViewPopularMovies.onScrollToEnd(movieLayoutManager, presenter.lastLoadedPage) {
            presenter.onMovieListScrolled(it)
            binding.recyclerViewPopularMovies.clearOnScrollListeners()
            Log.d(TAG, "onScrolledTo: page = $it")
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
            }
            false
        }
    }

    private fun subscribeToViewModels() {
        if (viewModelDisposable == null) {
            viewModelDisposable = presenter
                .observeViewModels()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel ->
                    onNewMovies(viewModel)
                }
        }
    }

    private fun unsubscribeFromViewModels() {
        viewModelDisposable?.dispose()
    }

    private fun onNewMovies(model: MovieViewModel) = with(model) {
        setProgressBarVisibility(state is MovieViewModel.State.Loading)
        movieItemAdapter.appendMovies(movies)
        binding.recyclerViewPopularMovies.onScrollToEnd(movieLayoutManager, presenter.lastLoadedPage) {
            presenter.onMovieListScrolled(it)
            binding.recyclerViewPopularMovies.clearOnScrollListeners()
            Log.d(TAG, "onScrolledTo: page = $it")
        }
    }

    private fun setProgressBarVisibility(isVisible: Boolean) {
        binding.progressBar.layoutProgressBar.isVisible = isVisible
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}