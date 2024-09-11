package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.ItunesApiClient
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.models.HistoryRepository
import com.practicum.playlistmaker.domain.models.TracksRepository

object Creator {
    private lateinit var application: Application

    fun init(app: Application) {
        this.application = app
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getHistoryRepository())
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