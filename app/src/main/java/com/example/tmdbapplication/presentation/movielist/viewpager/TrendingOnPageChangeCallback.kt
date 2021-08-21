package com.example.tmdbapplication.presentation.movielist.viewpager

import androidx.viewpager2.widget.ViewPager2
import com.example.tmdbapplication.presentation.model.MovieViewModel

open class TrendingOnPageChangeCallback(
    private val viewPager: ViewPager2,
    private val list: List<MovieViewModel>
) : ViewPager2.OnPageChangeCallback() {

    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
        if (state == ViewPager2.SCROLL_STATE_IDLE || state == ViewPager2.SCROLL_STATE_DRAGGING) {
            if (viewPager.currentItem == 0) {
                viewPager.setCurrentItem(list.size - 2, false)
            } else if (viewPager.currentItem == list.size - 1) {
                viewPager.setCurrentItem(1, false)
            }
        }
    }
}