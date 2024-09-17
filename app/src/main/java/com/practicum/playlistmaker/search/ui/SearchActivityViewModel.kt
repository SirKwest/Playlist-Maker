package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksConsumer
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class SearchActivityViewModel(
    private val historyInteractor: SearchHistoryInteractor, private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val searchScreenState = MutableLiveData<ScreenStates>()
    private val handler = Handler(Looper.getMainLooper())
    fun observeScreenState(): LiveData<ScreenStates> = searchScreenState

    fun clearHistory() {
        historyInteractor.clear()
    }

    fun getHistory(): List<Track> {
        return historyInteractor.get()
    }

    fun addToHistory(track: Track) {
        historyInteractor.add(track)
    }

    fun searchDebounce(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            updateScreenState(ScreenStates.ShowList(getHistory(), true))
        }
        val searchRunnable = Runnable { search(searchQuery) }
        handler.removeCallbacksAndMessages(REQUEST_TOKEN)
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, REQUEST_TOKEN, postTime)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(REQUEST_TOKEN)
    }

    private fun search(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            return
        }
        updateScreenState(ScreenStates.RequestInProgress)
        tracksInteractor.searchTracks(
            searchQuery,
            object : TracksConsumer {
                override fun consume(foundedTracks: List<Track>) {
                     handleResponse(foundedTracks)
                }

                override fun handleError(error: Exception) {
                    updateScreenState(ScreenStates.ConnectionIssues)
                }
            })
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
        private const val SEARCH_DEBOUNCE_DELAY = 3000L
        private val REQUEST_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchActivityViewModel(
                    Creator.provideSearchHistoryInteractor(),
                    Creator.provideTracksInteractor(),
                )
            }
        }
    }
}