package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        switchTheme((getCurrentTheme()!!))
    }

    private fun getCurrentTheme(): String? {
        return sharedPreferences.getString(THEME_NAME, ThemeValues.OS_THEME.toString())
    }

    private fun switchTheme(selectedTheme: String) {
        sharedPreferences.edit { putString(THEME_NAME, selectedTheme) }
        when (selectedTheme) {
            ThemeValues.DARK_THEME.toString() -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            ThemeValues.LIGHT_THEME.toString() -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            else -> {}
        }
    }

    fun switchTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            this.switchTheme(ThemeValues.DARK_THEME.toString())
        } else {
            this.switchTheme(ThemeValues.LIGHT_THEME.toString())
        }
    }

    companion object {
        const val PREFERENCES_NAME = "preferences_name"
        const val THEME_NAME = "selected_theme"
        enum class ThemeValues {
            DARK_THEME,
            LIGHT_THEME,
            OS_THEME
        }
    }
}