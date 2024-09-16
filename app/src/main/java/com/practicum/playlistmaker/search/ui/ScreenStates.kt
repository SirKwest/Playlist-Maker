package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface ScreenStates {
    data object ConnectionIssues: ScreenStates
    data object EmptyResults: ScreenStates
    data class ShowList(val tracks: List<Track>, val isHistory: Boolean): ScreenStates
    data object RequestInProgress: ScreenStates
    data object Default: ScreenStates
}