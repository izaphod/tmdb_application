package com.example.tmdbapplication.presentation.movielist.viewpager

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.tmdbapplication.R
import kotlin.math.abs

class TrendingPageTransformer : ViewPager2.PageTransformer {

    private var offset = 0F

    override fun transformPage(page: View, position: Float) {
        if (offset == 0F) {
            val cardWidth = page.findViewById<View>(R.id.root).width
            offset = -page.width / 2F + (cardWidth * 0.4F)
        }
        page.translationX = offset * position
        page.scaleY = 1 - (abs(position) * 0.1F)
    }
}