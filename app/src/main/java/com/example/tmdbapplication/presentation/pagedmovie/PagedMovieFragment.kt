package com.example.tmdbapplication.presentation.pagedmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.data.paging.MovieRequestType
import com.example.tmdbapplication.databinding.FragmentPagedMovieBinding
import com.example.tmdbapplication.presentation.pagedmovie.list.MoviePagingAdapter
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PagedMovieFragment : Fragment(R.layout.fragment_paged_movie) {

    private val args: PagedMovieFragmentArgs by navArgs()

    private val pagedMovieViewModel: PagedMovieViewModel by viewModels()

    private var _binding: FragmentPagedMovieBinding? = null
    private val binding get() = _binding!!

    private val pagedMovieAdapter = MoviePagingAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                PagedMovieFragmentDirections
                    .actionPagedMovieFragmentToMovieDetailsFragment(movie)
            )
        }
    ) { movie -> pagedMovieViewModel.manageSelectedInWatchlist(movie) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagedMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagedMovieViewModel.loadMovies(args.requestType)
        initViews(args.requestType)
        observeLoadStateFLow()
        observeViewModel()
    }

    private fun initViews(requestType: MovieRequestType) {

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.navigationIcon = ContextCompat
            .getDrawable(requireContext(), R.drawable.ic_arrow_back)

        when (requestType) {
            MovieRequestType.POPULAR -> {
                binding.toolbar.title = getString(R.string.pm_toolbar_title, POPULAR)
            }
            MovieRequestType.NOW_PLAYING -> {
                binding.toolbar.title = getString(R.string.pm_toolbar_title, NOW_PLAYING)
            }
            MovieRequestType.UPCOMING -> {
                binding.toolbar.title = getString(R.string.pm_toolbar_title, UPCOMING)
            }
        }
        binding.moviePagedList.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = pagedMovieAdapter
        }
    }

    private fun observeViewModel() {
        pagedMovieViewModel.movies.observe(viewLifecycleOwner, Observer { pagingData ->
            pagedMovieAdapter.submitData(lifecycle, pagingData)
        })
    }

    private fun observeLoadStateFLow() {
        lifecycleScope.launch {
            pagedMovieAdapter.loadStateFlow
                .collectLatest { loadStates ->
                    binding.pagedProgress.layoutProgressBar
                        .setVisibility(loadStates.refresh is LoadState.Loading)
                    binding.moviePagedList.setVisibility(loadStates.refresh !is LoadState.Loading)
                }
        }
    }

    companion object {
        private const val POPULAR = "Popular movies"
        private const val NOW_PLAYING = "Now playing movies"
        private const val UPCOMING = "Upcoming movies"
    }
}