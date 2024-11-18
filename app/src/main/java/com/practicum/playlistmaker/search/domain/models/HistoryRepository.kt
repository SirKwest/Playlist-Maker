package com.practicum.playlistmaker.search.domain.models

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun clear()
    fun add(track: Track)
    fun get(): Flow<List<Track>>

    companion object {
        const val HISTORY_KEY = "searched_tracks"
    }
}