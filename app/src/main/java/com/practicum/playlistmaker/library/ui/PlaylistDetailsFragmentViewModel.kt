package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private val playlistLiveData = MutableLiveData<Playlist>()
    private val tracksLiveData = MutableLiveData<List<Track>>()

    fun observePlaylistState(): LiveData<Playlist> = playlistLiveData
    fun observeTracksState(): LiveData<List<Track>> = tracksLiveData

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect { playlist ->
                playlistLiveData.postValue(playlist)
                if (playlist.addedTrackIds.isEmpty().not()) {
                    favoritesInteractor.getAllFavorites().collect {tracks ->
                        tracksLiveData.postValue(tracks)
                    }
                }
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist?) {
        if (playlist == null) {
            return
        }
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track, playlist)
        }
    }

    fun calculateCombinedTracksDuration(tracks: List<Track>, format: String) : String {
        val totalDurationMills = tracks.sumBy { it.trackTimeMillis?.toInt()!! }
        return SimpleDateFormat(format, Locale.getDefault()).format(totalDurationMills)
    }
}
