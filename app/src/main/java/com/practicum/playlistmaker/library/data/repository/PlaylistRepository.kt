package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun getPlaylists() : Flow<List<Playlist>>

    suspend fun getPlaylistById(id: Int) : Flow<Playlist>

    suspend fun getPlaylistIdsByTrackId(trackId: Int) : Flow<List<Int>>

    suspend fun getTracksByIds(ids: List<Int>) : Flow<List<Track>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist)

    suspend fun deletePlaylistById(id: Int)
}