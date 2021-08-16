package com.example.tmdbapplication.presentation.searchmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.data.paging.SearchPagingSource
import com.example.tmdbapplication.databinding.FragmentSearchMovieBinding
import com.example.tmdbapplication.presentation.pagedmovie.list.MoviePagingAdapter
import com.example.tmdbapplication.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchMovieFragment : Fragment(R.layout.fragment_search_movie) {

    private val searchMovieViewModel: SearchMovieViewModel by viewModels()

    private var _binding: FragmentSearchMovieBinding? = null
    private val binding get() = _binding!!

    private val searchResultAdapter = MoviePagingAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                SearchMovieFragmentDirections
                    .actionSearchMovieFragmentToMovieDetailsFragment(movie)
            )
        }
    ) { movie -> searchMovieViewModel.manageSelectedInWatchlist(movie) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initListeners()
        observeLoadStateFLow()
        observeViewModel()
    }

    private fun initRecyclerView() {
        binding.searchResultList.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = searchResultAdapter
        }
    }

    private fun initListeners() {
        binding.searchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovieViewModel.updateQuery(binding.searchQuery.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun observeLoadStateFLow() {
        lifecycleScope.launch {
            searchResultAdapter
                .loadStateFlow
                .collectLatest { loadStates ->
                    if (loadStates.refresh is LoadState.Loading) {
                        binding.searchProgress.layoutProgressBar.setVisibility(true)
                    } else {
                        binding.searchProgress.layoutProgressBar.setVisibility(false)
                        val errorState = when {
                            loadStates.prepend is LoadState.Error ->
                                loadStates.prepend as LoadState.Error
                            loadStates.append is LoadState.Error ->
                                loadStates.append as LoadState.Error
                            loadStates.refresh is LoadState.Error ->
                                loadStates.refresh as LoadState.Error
                            else -> null
                        }
                        binding.emptyResult.setVisibility(
                            errorState?.error?.message == SearchPagingSource.NOTHING_FOUND
                        )
                    }
                }
        }
    }

    private fun observeViewModel() {
        searchMovieViewModel.movies.observe(viewLifecycleOwner, Observer { pagingData ->
            searchResultAdapter.submitData(lifecycle, pagingData)
        })

        searchMovieViewModel.showInitialMessage.observe(viewLifecycleOwner, Observer { isVisible ->
            binding.initialMessage.setVisibility(isVisible)
        })

        searchMovieViewModel.showEmptyQuery.observe(viewLifecycleOwner, Observer { isQueryEmpty ->
            if (isQueryEmpty) {
                showEmptyQueryToast()
                searchMovieViewModel.showEmptyQueryComplete()
            }
        })
    }

    private fun showEmptyQueryToast() {
        Toast.makeText(context, getString(R.string.empty_query_toast), Toast.LENGTH_SHORT)
            .show()
    }
}