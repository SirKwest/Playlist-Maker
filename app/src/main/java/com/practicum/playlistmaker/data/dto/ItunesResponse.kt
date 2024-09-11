package com.practicum.playlistmaker.data.dto

class ItunesResponse(val results: ArrayList<Track>, val resultsCount: Int): Response() {
}