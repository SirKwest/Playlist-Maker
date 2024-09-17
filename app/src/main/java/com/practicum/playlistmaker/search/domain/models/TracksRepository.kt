package com.practicum.playlistmaker.search.domain.models

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}
