package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.dto.ItunesResponse
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequestSuspend(TrackSearchRequest(expression))
        if (response.resultCode != 200) {
            emit(null)
            return@flow
        }
        with(response as ItunesResponse) {
            val data = results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl,
                    false, //TODO
                )
            }
            emit(data)
        }
    }
}
