package com.practicum.playlistmaker.library.data.repository

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.TrackDbConverter
import com.practicum.playlistmaker.library.data.db.TrackEntity
import com.practicum.playlistmaker.library.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {
    override fun getAllFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesDao().getTracks()
        emit(convertTrackFromDbEntity(tracks))
    }

    override fun addToFavorite(track: Track) {
        appDatabase.favoritesDao().insertTrack(trackDbConverter.map(track))
    }

    override fun removeFromFavorite(trackId: Int) {
        appDatabase.favoritesDao().removeTrack(trackId)
    }

    private fun convertTrackFromDbEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}
