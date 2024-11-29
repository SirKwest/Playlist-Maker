package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {
    override fun getAllFavorites(): Flow<List<Track>> {
        return repository.getAllFavorites()
    }

    override fun addToFavorites(track: Track) {
        repository.addToFavorite(track)
    }

    override fun removeFromFavorites(trackId: Int) {
        repository.removeFromFavorite(trackId)
    }

    override fun removeTrackRecord(trackId: Int) {
        repository.removeTrackRecord(trackId)
    }

}