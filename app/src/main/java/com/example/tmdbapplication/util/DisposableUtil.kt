package com.example.tmdbapplication.util

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Disposable.disposeIfNeeded(compositeDisposable: CompositeDisposable) {
    if (compositeDisposable.delete(this)) {
        if (!this.isDisposed) this.dispose()
    }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable {
    compositeDisposable.add(this)
    return this
}