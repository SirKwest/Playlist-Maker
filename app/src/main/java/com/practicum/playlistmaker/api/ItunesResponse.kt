package com.practicum.playlistmaker.api

import com.practicum.playlistmaker.track.Track

class ItunesResponse(val results: ArrayList<Track>, val resultsCount: Int) {
}