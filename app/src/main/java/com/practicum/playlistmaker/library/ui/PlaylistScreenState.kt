package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistScreenState {
    data object EmptyScreen : PlaylistScreenState
    data class ShowPlaylists(val playlists: List<Playlist>) : PlaylistScreenState
}