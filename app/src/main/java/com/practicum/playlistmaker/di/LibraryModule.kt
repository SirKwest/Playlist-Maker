package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.ui.FavoritesFragmentViewModel
import com.practicum.playlistmaker.library.ui.PlaylistFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val libraryModule = module {

    viewModelOf(::FavoritesFragmentViewModel)
    viewModelOf(::PlaylistFragmentViewModel)
}
