package com.plb.conference

import android.app.Application
import com.plb.conference.data.UserPreferencesRepository

class ReceptionApplication : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(this)
    }
}