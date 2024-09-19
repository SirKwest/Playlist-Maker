package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.search.data.network.ItunesApiClient
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.models.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.TracksRepository
import com.practicum.playlistmaker.search.ui.SearchActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {

    single {
        androidContext().getSharedPreferences(HistoryRepository.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        ItunesApiClient()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    viewModel {
        SearchActivityViewModel(get<SearchHistoryInteractor>(), get<TracksInteractor>())
    }
}