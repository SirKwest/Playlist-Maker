package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {
    private lateinit var themePreferences: SharedPreferences
    private lateinit var searchPreferences: SharedPreferences

    private var playerActivityTurnedOn: Boolean = false

    override fun onCreate() {
        super.onCreate()
        themePreferences = getSharedPreferences(THEME_PREFERENCES_NAME, Context.MODE_PRIVATE)
        searchPreferences = getSharedPreferences(SearchHistory.PREFERENCES_NAME, Context.MODE_PRIVATE)
        switchTheme(isDarkTheme())
    }

    fun getSearchPreferences(): SharedPreferences {
        return searchPreferences
    }

    fun isDarkTheme(): Boolean {
        return themePreferences.getBoolean(THEME_KEY, false)
    }

    public fun playerActivityResumed() {
        playerActivityTurnedOn = true
    }

    public fun playerActivityPaused() {
        playerActivityTurnedOn = false
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
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