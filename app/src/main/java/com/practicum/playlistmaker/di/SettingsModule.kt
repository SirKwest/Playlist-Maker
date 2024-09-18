package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.ThemeInteractorImpl
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.sharing.data.SharingImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    single {
        androidContext().getSharedPreferences(ThemeInteractor.THEME_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    factory<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingImpl(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}
