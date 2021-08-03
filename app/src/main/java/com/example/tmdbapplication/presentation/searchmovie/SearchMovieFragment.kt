package com.example.tmdbapplication.presentation.searchmovie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapplication.R
import com.example.tmdbapplication.TmdbApplication
import com.example.tmdbapplication.databinding.FragmentSearchMovieBinding
import com.example.tmdbapplication.presentation.model.MovieViewModel
import com.example.tmdbapplication.presentation.movielist.list.MoviePagingAdapter
import com.example.tmdbapplication.util.setVisibility
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchMovieFragment : MvpAppCompatFragment(R.layout.fragment_search_movie), SearchMovieView {

    @Inject
    lateinit var presenterProvider: Provider<SearchMoviePresenter>
    private val presenter: SearchMoviePresenter by moxyPresenter { presenterProvider.get() }

    private var _binding: FragmentSearchMovieBinding? = null
    private val binding get() = _binding!!

    private val searchResultAdapter = MoviePagingAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                SearchMovieFragmentDirections
                    .actionSearchMovieFragmentToMovieDetailsFragment(movie)
            )
        }
    ) { movie -> presenter.onItemWatchlistPressed(movie) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TmdbApplication.instance.appComponent?.injectFragment(this)
    }

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
        addLoadStateListener()
    }

    override fun showSearchResult(pagingData: PagingData<MovieViewModel>) {
        searchResultAdapter.submitData(lifecycle, pagingData)
    }

    override fun setEmptyScreenVisibility(isVisible: Boolean) {
        binding.emptyQuery.setVisibility(isVisible)
    }

    override fun showEmptyQueryToast() {
        Toast.makeText(context, getString(R.string.empty_query_toast), Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView() {
        binding.searchResultList.adapter = searchResultAdapter
        binding.searchResultList.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
    }

    private fun initListeners() {
        binding.searchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.onSearchPressed(binding.searchQuery.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun addLoadStateListener() {
        searchResultAdapter.addLoadStateListener { loadStates ->
            if (loadStates.refresh is LoadState.Loading) {
                binding.progress.layoutProgressBar.setVisibility(true)
            } else {
                binding.progress.layoutProgressBar.setVisibility(false)

                // TODO: 8/2/21 add error state screen
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
}