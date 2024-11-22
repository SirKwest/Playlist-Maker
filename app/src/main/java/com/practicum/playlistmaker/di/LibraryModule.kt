package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.db.TrackDbConverter
import com.practicum.playlistmaker.library.data.repository.FavoritesRepositoryImpl
import com.practicum.playlistmaker.library.data.repository.PlaylistRepository
import com.practicum.playlistmaker.library.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.FavoritesRepository
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.library.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.library.ui.FavoritesFragmentViewModel
import com.practicum.playlistmaker.library.ui.PlaylistFragmentViewModel
import com.practicum.playlistmaker.library.ui.PlaylistCreationFragmentViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val libraryModule = module {

    viewModelOf(::FavoritesFragmentViewModel)
    viewModelOf(::PlaylistFragmentViewModel)
    viewModelOf(::PlaylistCreationFragmentViewModel)

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build() }

    factory { TrackDbConverter() }

    factory { PlaylistDbConverter(get()) }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}
