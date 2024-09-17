package com.practicum.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isDarkThemeSelected(): Boolean
    fun saveTheme(isDarkThemeEnabled: Boolean)

    companion object {
        const val THEME_PREFERENCES_NAME = "theme_preferences"
    }

}