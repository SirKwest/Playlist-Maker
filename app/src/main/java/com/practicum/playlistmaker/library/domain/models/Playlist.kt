package com.practicum.playlistmaker.library.domain.models

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String,
    val addedTrackIds: List<Int>
)
