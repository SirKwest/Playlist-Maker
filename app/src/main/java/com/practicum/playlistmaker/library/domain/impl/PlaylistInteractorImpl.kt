package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.data.repository.PlaylistRepository
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        return playlistRepository.addTrackToPlaylist(track, playlist)
    }

}