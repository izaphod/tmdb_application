package com.example.tmdbapplication

import android.app.Application
import com.example.tmdbapplication.di.component.AppComponent
import com.example.tmdbapplication.di.component.DaggerAppComponent

class TmdbApplication : Application() {

    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        initDagger()
    }

    override fun onTerminate() {
        appComponent = null
        super.onTerminate()
    }

    private fun initDagger() {
        if (appComponent == null) {
            this.appComponent = DaggerAppComponent.factory().create(this)
        }
        appComponent?.inject(instance)
    }

    companion object {
        lateinit var instance: TmdbApplication
    }
}