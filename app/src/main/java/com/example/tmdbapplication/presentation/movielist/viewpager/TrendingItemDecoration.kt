package com.example.tmdbapplication.presentation.movielist.viewpager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TrendingItemDecoration :
    RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int = 64

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }
}