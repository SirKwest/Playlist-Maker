package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.library.domain.models.Playlist

sealed interface BottomSheetPlaylistsState {
    data object Empty : BottomSheetPlaylistsState

    data class ShowPlaylists(val playlists: List<Playlist>) : BottomSheetPlaylistsState
}