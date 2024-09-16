package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
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

class SearchActivityViewModel(private val historyInteractor: SearchHistoryInteractor, private val tracksInteractor: TracksInteractor) : ViewModel() {
    private val searchScreenState = MutableLiveData<ScreenStates>()
    fun observeScreenState(): LiveData<ScreenStates> = searchScreenState

    private val handler = Handler(Looper.getMainLooper())
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
        val searchRunnable = Runnable { search(searchQuery) }
        handler.removeCallbacksAndMessages(searchRunnable)
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            REQUEST_TOKEN,
            postTime,
        )
        //handler.postDelayed(searchRunnable, SearchActivity.SEARCH_DEBOUNCE_DELAY)
    }

    private fun search(searchQuery: String) {
        Log.i("VIEWMODEL", "Search of " + searchQuery + " started")
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

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(REQUEST_TOKEN)
    }
    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 3000L
        val REQUEST_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchActivityViewModel(Creator.provideSearchHistoryInteractor(), Creator.provideTracksInteractor())
            }
        }
    }
}