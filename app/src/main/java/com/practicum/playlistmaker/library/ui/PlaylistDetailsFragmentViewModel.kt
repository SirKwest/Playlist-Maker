package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistDetailsFragmentViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val playlistLiveData = MutableLiveData<Playlist>()
    private val tracksLiveData = MutableLiveData<List<Track>>()

    fun observePlaylistState(): LiveData<Playlist> = playlistLiveData
    fun observeTracksState(): LiveData<List<Track>> = tracksLiveData

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect { playlist ->
                playlistLiveData.postValue(playlist)
                if (playlist.addedTrackIds.isEmpty().not()) {
                    /*playlistInteractor.getTracksByIds(playlist.addedTrackIds).collect { tracks ->
                        tracksLiveData.postValue(tracks)
                    }*/
                }
            }
        }
    }
}
