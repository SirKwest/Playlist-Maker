package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getAllFavorites(): Flow<List<Track>>
    fun addToFavorites(track: Track)
    fun removeFromFavorites(trackId: Int)
}