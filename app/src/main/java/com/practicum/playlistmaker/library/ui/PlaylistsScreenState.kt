package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistsScreenState {
    data object EmptyScreen : PlaylistsScreenState
    data class ShowPlaylists(val playlists: List<Playlist>) : PlaylistsScreenState
}