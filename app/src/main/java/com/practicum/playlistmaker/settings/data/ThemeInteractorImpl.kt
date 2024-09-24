package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.ThemeInteractor

class ThemeInteractorImpl(private val sharedPreferences: SharedPreferences): ThemeInteractor {

    private var isDarkTheme = false
    override fun isDarkThemeSelected(): Boolean {
        return sharedPreferences.getBoolean(App.THEME_KEY, isDarkTheme)
    }

    override fun saveTheme(isDarkThemeEnabled: Boolean) {
        sharedPreferences.edit { putBoolean(App.THEME_KEY, isDarkThemeEnabled) }
        isDarkTheme = isDarkThemeEnabled
    }
}

