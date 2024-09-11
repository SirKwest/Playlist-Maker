package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun get(): List<Track>
    fun add(track: Track)

    fun clear()
}