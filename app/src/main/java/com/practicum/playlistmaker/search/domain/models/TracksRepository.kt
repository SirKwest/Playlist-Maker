package com.practicum.playlistmaker.search.domain.models

import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<List<Track>?>
}
