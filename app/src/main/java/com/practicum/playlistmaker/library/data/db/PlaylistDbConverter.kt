package com.practicum.playlistmaker.library.data.db

import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter(private val appDatabase: AppDatabase) {
    fun map(playlist: Playlist) : PlaylistEntity {
        return PlaylistEntity(playlist.id, playlist.name, playlist.description, playlist.imagePath)
    }

    suspend fun map(playlistEntity: PlaylistEntity) : Playlist {
        val associatedTracks = appDatabase.tracksDao().getAllTrackIdsByPlaylistId(playlistEntity.id)
        return Playlist(playlistEntity.id, playlistEntity.name, playlistEntity.description, playlistEntity.path, associatedTracks)
    }
}