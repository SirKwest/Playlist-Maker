package com.practicum.playlistmaker.domain.models

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String?,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?, //2018-02-09T12:00:00Z
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
)