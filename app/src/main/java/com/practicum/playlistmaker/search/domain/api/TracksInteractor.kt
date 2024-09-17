package com.practicum.playlistmaker.search.domain.api

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
}
