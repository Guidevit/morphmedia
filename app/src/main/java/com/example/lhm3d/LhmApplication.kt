package com.example.lhm3d

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Application class for LHM3D app
 * Handles initialization of Firebase and other app-level components
 */
class LhmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}