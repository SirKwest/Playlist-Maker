package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.ThemeInteractor

class ThemeInteractorImpl(private val sharedPreferences: SharedPreferences): ThemeInteractor {
    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun switchTheme(isDarkThemeEnabled: Boolean) {
        sharedPreferences.edit { putBoolean(THEME_KEY, isDarkThemeEnabled) }
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val THEME_KEY = "dark_theme"
    }

}