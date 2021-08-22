package com.example.tmdbapplication.presentation.searchmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    private lateinit var searchView: SearchView

    private val searchResultAdapter = MoviePagingAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                SearchMovieFragmentDirections.actionSearchToDetails(movie)
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
        initViews()
        initListeners()
        observeLoadStateFLow()
        observeViewModel()
    }

    private fun initViews() {
        binding.toolbarSearch.inflateMenu(R.menu.menu_search)
        searchView = binding.toolbarSearch.menu.findItem(R.id.search).actionView as SearchView
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = getString(R.string.search_movie_hint)
        searchView.maxWidth = Int.MAX_VALUE

        binding.searchResultList.apply {
            layoutManager =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = searchResultAdapter
        }
    }

    private fun initListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchMovieViewModel.updateQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
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
        searchMovieViewModel.movies.observe(viewLifecycleOwner) { pagingData ->
            searchResultAdapter.submitData(lifecycle, pagingData)
        }
        searchMovieViewModel.showInitialMessage.observe(viewLifecycleOwner) { isVisible ->
            binding.initialMessage.setVisibility(isVisible)
        }

        searchMovieViewModel.showEmptyQuery.observe(viewLifecycleOwner) { isQueryEmpty ->
            if (isQueryEmpty) {
                showEmptyQueryToast()
                searchMovieViewModel.showEmptyQueryComplete()
            }
        }
    }

    private fun showEmptyQueryToast() {
        Toast.makeText(context, getString(R.string.empty_query_toast), Toast.LENGTH_SHORT)
            .show()
    }
}