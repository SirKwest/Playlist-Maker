package com.practicum.playlistmaker.library.data.db

import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConverter {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis.orEmpty(),
            track.artworkUrl100,
            track.collectionName.orEmpty(),
            track.releaseDate.orEmpty(),
            track.primaryGenreName,
            track.country.orEmpty(),
            track.previewUrl.orEmpty(),
            System.currentTimeMillis().toString()
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            true
        )
    }
}