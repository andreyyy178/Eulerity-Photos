package com.example.eulerityphotos

import android.app.Application
import com.example.eulerityphotos.data.AppContainer
import com.example.eulerityphotos.data.DefaultAppContainer

class PhotosApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}