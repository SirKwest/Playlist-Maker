package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, Exception?>> {
        return repository.searchTracks(expression).map { result ->
            if (result === null) {
                Pair(null, Exception("Error at search request"))
            } else {
                Pair(result, null)
            }
        }
    }
}
