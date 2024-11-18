package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun get(): Flow<List<Track>>
    fun add(track: Track)

    fun clear()
}