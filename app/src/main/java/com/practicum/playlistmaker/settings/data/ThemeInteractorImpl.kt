package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.ThemeInteractor

class ThemeInteractorImpl(private val sharedPreferences: SharedPreferences): ThemeInteractor {

    private var isDarkTheme = false
    override fun isDarkThemeSelected(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, isDarkTheme)
    }

    override fun saveTheme(isDarkThemeEnabled: Boolean) {
        sharedPreferences.edit { putBoolean(THEME_KEY, isDarkThemeEnabled) }
        isDarkTheme = isDarkThemeEnabled
    }

    companion object {
        const val THEME_KEY = "dark_theme"
    }
}
