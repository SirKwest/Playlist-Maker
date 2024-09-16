package com.practicum.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun switchTheme(isDarkThemeEnabled: Boolean)

    companion object {
        const val THEME_PREFERENCES_NAME = "theme_preferences"
    }

}