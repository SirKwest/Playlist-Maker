package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteScreenState {
    data object EmptyScreen : FavoriteScreenState
    data class ShowFavorites(val favorites: List<Track>) : FavoriteScreenState
}