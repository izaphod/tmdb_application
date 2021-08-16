package com.example.tmdbapplication.util

fun String.formatBackdropPath(): String {
    return "${IMAGE_PATH}w1280$this"
}

fun String.formatPosterPath(): String {
    return "${IMAGE_PATH}w342$this"
}

const val IMAGE_PATH = "https://image.tmdb.org/t/p/"