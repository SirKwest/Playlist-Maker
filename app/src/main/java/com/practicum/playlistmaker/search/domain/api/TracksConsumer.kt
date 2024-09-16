package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksConsumer {
    fun consume(foundedTracks: List<Track>)
    fun handleError(error: Exception)
}