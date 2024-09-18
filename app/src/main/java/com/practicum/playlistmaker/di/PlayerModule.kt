package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val playerModule = module {

    factory {
        MediaPlayer()
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get(), get())
    }

    viewModel { (url: String) ->
        PlayerViewModel(get(parameters = { parametersOf(url) }))
    }
}
