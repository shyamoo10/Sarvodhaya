package com.sarvodhaya.store

import android.app.Application
import com.sarvodhaya.store.di.AppContainer

class SarvodhayaApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
