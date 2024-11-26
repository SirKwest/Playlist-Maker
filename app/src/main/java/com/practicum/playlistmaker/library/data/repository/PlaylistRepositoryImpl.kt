package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.data.db.PlaylistsTracksEntity
import com.practicum.playlistmaker.library.data.db.TrackEntity
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val appDatabase: AppDatabase, private val converter: PlaylistDbConverter) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(converter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playLists = appDatabase.playlistDao().getPlaylists()
        emit(convertPlaylistFromDbEntity(playLists))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        appDatabase.tracksDao().insertRecord(PlaylistsTracksEntity(0, playlist.id, track.trackId))
    }

    private suspend fun convertPlaylistFromDbEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist ->

            converter.map(playlist)
        }
    }
}