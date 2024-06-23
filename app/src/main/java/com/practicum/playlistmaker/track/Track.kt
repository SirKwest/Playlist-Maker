package com.practicum.playlistmaker.track

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?,
    val releaseDate: String?, //2018-02-09T12:00:00Z
    val primaryGenreName: String?,
    val country: String?,
)