package com.example.carlogger

import android.app.Application
import com.example.carlogger.data.local.CarLoggerDatabase

class CarLoggerApplication : Application() {

    // Lazy initialization of the database
    val database by lazy { CarLoggerDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        // Any application-wide initialization can go here
    }
}