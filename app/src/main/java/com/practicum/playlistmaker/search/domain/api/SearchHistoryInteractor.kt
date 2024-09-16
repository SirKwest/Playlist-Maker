package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun get(): List<Track>
    fun add(track: Track)

    fun clear()
}