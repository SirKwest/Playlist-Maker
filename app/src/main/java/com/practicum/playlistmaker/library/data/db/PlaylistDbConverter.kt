package com.practicum.playlistmaker.library.data.db

import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist) : PlaylistEntity {
        return PlaylistEntity(playlist.id, playlist.name, playlist.description, playlist.imagePath)
    }

    fun map(playlistEntity: PlaylistEntity) : Playlist {
        return Playlist(playlistEntity.id, playlistEntity.name, playlistEntity.description, playlistEntity.path, 0)
    }
}