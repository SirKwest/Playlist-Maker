package com.practicum.playlistmaker.search.data.dto

class ItunesResponse(val results: ArrayList<Track>, val resultsCount: Int): Response() {
}