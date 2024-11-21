package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.domain.models.Playlist

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
}