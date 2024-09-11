package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.dto.ItunesResponse
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode != 200) {
            return emptyList()
        }
        return (response as ItunesResponse).results.map {
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
                it.previewUrl
            )
        }
    }
}