package com.example.tmdbapplication.presentation.moviedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapplication.R
import com.example.tmdbapplication.databinding.FragmentMovieDetailsBinding
import com.example.tmdbapplication.presentation.movielist.list.MovieItemViewHolder

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
        showMovieDetails()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        args.viewModel.movie.backdropPath?.let { backdrop = BACKDROP_FORMATTED_PATH + it }
        Glide.with(this)
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
        args.viewModel.movie.posterPath?.let {
            poster = MovieItemViewHolder.POSTER_FORMATTED_PATH + it
        }
        Glide.with(this)
            .load(poster)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .centerCrop()
            .into(binding.moviePosterImage)
    }

    companion object {
        private const val BACKDROP_FORMATTED_PATH = "https://image.tmdb.org/t/p/w1280"
    }
}