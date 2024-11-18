package com.practicum.playlistmaker.search.domain.models

import java.text.SimpleDateFormat
import java.util.Locale

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
    var isFavorite: Boolean
) {
    fun getBigCover() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getReleaseYear() = releaseDate?.substring(0..3)
    fun getTrackTimeFormatted(format: String) = SimpleDateFormat(format, Locale.getDefault()).format(trackTimeMillis?.toLong())
}