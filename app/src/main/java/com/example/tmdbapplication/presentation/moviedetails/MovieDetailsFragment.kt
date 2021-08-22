package com.example.tmdbapplication.presentation.moviedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.FragmentMovieDetailsBinding
import com.example.tmdbapplication.di.module.GlideApp
import com.example.tmdbapplication.presentation.MainActivity
import com.example.tmdbapplication.util.formatBackdropPath
import com.example.tmdbapplication.util.formatPosterPath

class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private val args: MovieDetailsFragmentArgs by navArgs()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        showMovieDetails()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initToolbar() {
        val toolbar = (activity as MainActivity).toolbar
        toolbar.title =
            getString(R.string.fragment_movie_details_label, args.viewModel.movie.title)
    }

    private fun showMovieDetails() {
        setBackdropImage()
        setPosterImage()
        binding.movieTitle.text = args.viewModel.movie.title
        binding.movieReleaseDate.text = args.viewModel.movie.releaseDate
        binding.movieOverview.text = args.viewModel.movie.overview
        binding.movieRating.userScore = args.viewModel.movie.rating
    }

    private fun setBackdropImage() {
        var backdrop: String? = null
        args.viewModel.movie.backdropPath?.let { backdrop = it.formatBackdropPath() }
        GlideApp.with(this)
            .load(backdrop)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .centerCrop()
            .into(binding.movieBackdropImage)
    }

    private fun setPosterImage() {
        var poster: String? = null
        args.viewModel.movie.posterPath?.let { poster = it.formatPosterPath() }
        GlideApp.with(this)
            .load(poster)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .centerCrop()
            .into(binding.moviePosterImage)
    }
}