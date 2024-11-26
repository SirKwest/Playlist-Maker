package com.practicum.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks_table")
data class PlaylistsTracksEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val playlistId: Int,
    val trackId: Int,
)
