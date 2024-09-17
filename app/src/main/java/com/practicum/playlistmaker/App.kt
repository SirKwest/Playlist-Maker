package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    private lateinit var themePreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        themePreferences = Creator.getSharedPreferences(THEME_PREFERENCES_NAME)
        switchTheme(themePreferences.getBoolean(THEME_KEY, false))
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
