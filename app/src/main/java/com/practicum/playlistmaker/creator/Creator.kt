package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.ItunesApiClient
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.models.HistoryRepository
import com.practicum.playlistmaker.domain.models.TracksRepository
import com.practicum.playlistmaker.player.data.PlayerImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
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
        return PlayerImpl(MediaPlayer(), url)
    }

    fun provideShareInteractor(): SharingInteractor {
        return SharingImpl(application.baseContext)
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
