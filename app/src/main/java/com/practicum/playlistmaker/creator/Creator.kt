package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.ItunesApiClient
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.models.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.TracksRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.settings.data.ThemeInteractorImpl
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.sharing.data.SharingImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

object Creator {
    private lateinit var application: Application

    fun init(app: Application) {
        application = app
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getHistoryRepository())
    }

    fun providePlayerInteractor(url: String): PlayerInteractor {
        return PlayerInteractorImpl(MediaPlayer(), url)
    }

    fun provideShareInteractor(): SharingInteractor {
        return SharingImpl(application.baseContext)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getSharedPreferences(ThemeInteractor.THEME_PREFERENCES_NAME))
    }

    fun getSharedPreferences(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
        return application.getSharedPreferences(name, mode)
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(ItunesApiClient())
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(getSharedPreferences(HistoryRepository.PREFERENCES_NAME))
    }
}
