package com.lernki.app

import android.app.Application
import com.lernki.app.di.AppContainer

/**
 * Application-Klasse. Haelt den zentralen [AppContainer], der alle
 * Repositories und Datenquellen bereitstellt (einfache manuelle DI,
 * ohne zusaetzliches Framework wie Hilt, damit das Projekt schlank bleibt).
 */
class LernKiApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
