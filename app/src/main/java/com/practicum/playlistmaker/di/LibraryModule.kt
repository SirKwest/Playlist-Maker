package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.TrackDbConverter
import com.practicum.playlistmaker.library.data.repository.FavoritesRepositoryImpl
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.FavoritesRepository
import com.practicum.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.library.ui.FavoritesFragmentViewModel
import com.practicum.playlistmaker.library.ui.PlaylistFragmentViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val libraryModule = module {

    viewModelOf(::FavoritesFragmentViewModel)
    viewModelOf(::PlaylistFragmentViewModel)

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build() }

    factory { TrackDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}
