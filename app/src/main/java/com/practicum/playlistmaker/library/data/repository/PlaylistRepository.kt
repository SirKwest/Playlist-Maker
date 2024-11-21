package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun getPlaylists() : Flow<List<Playlist>>
}