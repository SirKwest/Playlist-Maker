package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.models.Playlist

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist)
}