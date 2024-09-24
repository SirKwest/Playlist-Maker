package com.practicum.playlistmaker.search.domain.models

interface HistoryRepository {
    fun clear()
    fun add(track: Track)
    fun get(): List<Track>

    companion object {
        const val HISTORY_KEY = "searched_tracks"
    }
}