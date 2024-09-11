package com.practicum.playlistmaker.domain.models

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}
