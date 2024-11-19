package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryInteractorImpl(private val repository: HistoryRepository):
    SearchHistoryInteractor {
    override fun get(): Flow<List<Track>> = flow {
        repository.get().collect { emit(it) }
    }

    override fun add(track: Track) {
        repository.add(track)
    }

    override fun clear() {
        repository.clear()
    }
}