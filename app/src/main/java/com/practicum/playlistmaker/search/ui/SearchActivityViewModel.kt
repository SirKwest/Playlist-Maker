package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchActivityViewModel(
    private val historyInteractor: SearchHistoryInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val searchScreenState = MutableLiveData<ScreenStates>()
    private var searchJob: Job? = null

    fun observeScreenState(): LiveData<ScreenStates> = searchScreenState

    fun clearHistory() {
        historyInteractor.clear()
    }

    fun getHistory() {
        viewModelScope.launch {
            historyInteractor.get().collect {
                if (it.isNotEmpty()) {
                    updateScreenState(ScreenStates.ShowList(it, true))
                } else {
                    updateScreenState(ScreenStates.Default)
                }
            }
        }
    }

    fun addToHistory(track: Track) {
        historyInteractor.add(track)
    }

    fun searchDebounce(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            getHistory()
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY_MILLS)
            search(searchQuery)
        }
    }

    private fun search(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            return
        }
        updateScreenState(ScreenStates.RequestInProgress)
        viewModelScope.launch {
            tracksInteractor.searchTracks(searchQuery).collect { pair ->
                if (pair.second === null) {
                    handleResponse(pair.first!!)
                } else {
                    updateScreenState(ScreenStates.ConnectionIssues)
                }
            }
        }
    }
    private fun handleResponse(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            updateScreenState(ScreenStates.EmptyResults)
            return
        }
        updateScreenState(ScreenStates.ShowList(tracks, false))
    }

    private fun updateScreenState(state: ScreenStates) {
        searchScreenState.postValue(state)
    }

    companion object {
        private const val SEARCH_DELAY_MILLS = 3000L
    }
}
