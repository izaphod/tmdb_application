package com.example.tmdbapplication.presentation.movielists.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.onScrollToMiddle(
    linearLayoutManager: LinearLayoutManager,
    lastLoadedPage: Int,
    onScrolledTo: (Int) -> Unit
) = addOnScrollListener(object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = linearLayoutManager.itemCount
        val visibleItemCount = linearLayoutManager.childCount
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        if (firstVisibleItemPosition + visibleItemCount >= totalItemCount - 3) {
            onScrolledTo(lastLoadedPage + 1)
        }
    }
})