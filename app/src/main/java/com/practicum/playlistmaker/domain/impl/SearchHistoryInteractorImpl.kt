package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.models.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: HistoryRepository): SearchHistoryInteractor {
    override fun get(): List<Track> {
        return repository.get()
    }

    override fun add(track: Track) {
        repository.add(track)
    }

    override fun clear() {
        repository.clear()
    }
}