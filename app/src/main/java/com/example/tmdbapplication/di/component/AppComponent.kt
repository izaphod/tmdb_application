package com.example.tmdbapplication.di.component

import android.content.Context
import com.example.tmdbapplication.TmdbApplication
import com.example.tmdbapplication.di.module.PresenterModule
import com.example.tmdbapplication.presentation.movielists.MovieListsFragment
import com.example.tmdbapplication.presentation.watchlist.WatchlistFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [PresenterModule::class])
interface AppComponent : AndroidInjector<TmdbApplication> {

    override fun inject(instance: TmdbApplication)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun injectFragment(fragment: MovieListsFragment)
    fun injectFragment(fragment: WatchlistFragment)
}