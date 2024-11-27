package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<Track>>
    fun addToFavorite(track: Track)
    fun removeFromFavorite(trackId: Int)

    fun removeTrackRecord(trackId: Int)
}