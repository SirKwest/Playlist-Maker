package com.practicum.playlistmaker.domain.models

interface HistoryRepository {
    fun clear()
    fun add(track: Track)
    fun get(): List<Track>

    companion object {
        const val PREFERENCES_NAME = "search_history"
        const val HISTORY_KEY = "searched_tracks"
    }
}