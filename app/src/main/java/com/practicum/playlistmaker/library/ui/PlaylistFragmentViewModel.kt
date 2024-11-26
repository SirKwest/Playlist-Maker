package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistFragmentViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val playlistsStateLiveData = MutableLiveData<PlaylistScreenState>()

    val observePlaylistState: LiveData<PlaylistScreenState> = playlistsStateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    updateState(PlaylistScreenState.EmptyScreen)
                } else {
                    updateState(PlaylistScreenState.ShowPlaylists(playlists))
                }
            }
        }
    }

    private fun updateState(state: PlaylistScreenState) {
        playlistsStateLiveData.postValue(state)
    }
}
