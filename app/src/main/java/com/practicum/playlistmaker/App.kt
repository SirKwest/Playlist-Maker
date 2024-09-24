package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.di.libraryModule
import com.practicum.playlistmaker.di.playerModule
import com.practicum.playlistmaker.di.searchModule
import com.practicum.playlistmaker.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var themePreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(settingsModule, playerModule, searchModule, libraryModule)
        }

        themePreferences = baseContext.getSharedPreferences(THEME_PREFERENCES_NAME, Context.MODE_PRIVATE)
        switchTheme(themePreferences.getBoolean(THEME_KEY, false))
    }

    private fun switchTheme(isDarkThemeEnabled: Boolean) {
        themePreferences.edit { putBoolean(THEME_KEY, isDarkThemeEnabled) }
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val THEME_PREFERENCES_NAME = "theme_preferences"
        const val THEME_KEY = "dark_theme"
    }
}
