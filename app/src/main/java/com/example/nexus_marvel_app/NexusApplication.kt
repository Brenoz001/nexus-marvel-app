package com.example.nexus_marvel_app

import android.app.Application
import com.example.nexus_marvel_app.di.Graph

class NexusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.init(this)
    }
}
