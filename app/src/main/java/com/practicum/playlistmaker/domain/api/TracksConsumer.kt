package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksConsumer {
    fun consume(foundedTracks: List<Track>)
    fun handleError(error: Exception)
}